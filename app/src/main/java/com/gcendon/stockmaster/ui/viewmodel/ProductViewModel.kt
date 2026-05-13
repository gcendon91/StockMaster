package com.gcendon.stockmaster.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gcendon.stockmaster.data.AppUser
import com.gcendon.stockmaster.data.Category
import com.gcendon.stockmaster.data.Product
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val db = Firebase.firestore // Referencia a la base de datos
    private val auth = FirebaseAuth.getInstance()

    //Estado para los miembros
    private val _members = MutableStateFlow<List<AppUser>>(emptyList())
    val members = _members.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    var dynamicEmojiMap by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    private val _products = MutableStateFlow<List<Product>?>(null)
    val products: StateFlow<List<Product>?> = _products.asStateFlow()

    var householdId by mutableStateOf<String?>(null)
        private set

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    val shoppingList: StateFlow<List<Product>> = products.map { lista ->
        lista?.filter { it.currentStock < it.minStock } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var inviteCode by mutableStateOf<String?>(null)
        private set

    var hasSeenOnboarding by mutableStateOf(true) // Por defecto true para no molestar si hay error
        private set

    val DEFAULT_CATEGORIES = listOf(
        "Almacén",
        "Bebidas",
        "Carnicería",
        "Lácteos",
        "Limpieza",
        "Pescadería",
        "Verdulería",
        "Otros",
    )

    fun String.capitalizeFirst(): String = this.lowercase().replaceFirstChar { it.uppercase() }

    init {
        auth.currentUser?.uid?.let { uid ->
            setupUserAndHousehold(uid)
        }
        listenToEmojiConfig()
    }

    fun listenToMembers() {
        val hId = householdId ?: return
        db.collection("users")
            .whereEqualTo("householdId", hId)
            .addSnapshotListener { snapshot, _ ->
                val membersList = snapshot?.documents?.mapNotNull { doc ->
                    val email = doc.getString("email") ?: ""
                    val storedName = doc.getString("displayName")

                    // LÓGICA DE EMERGENCIA:
                    // Si el nombre es nulo, vacío o el genérico, sacamos el nombre del email
                    val finalName =
                        if (storedName.isNullOrBlank() || storedName == "Usuario Master") {
                            email.substringBefore("@").replaceFirstChar { it.uppercase() }
                        } else {
                            storedName
                        }

                    AppUser(
                        uid = doc.id,
                        displayName = finalName,
                        email = email,
                        photoUrl = doc.getString("photoUrl") ?: ""
                    )
                } ?: emptyList()

                _members.value = membersList
            }
    }

    fun removeUserFromHousehold(targetUid: String) {
        db.collection("users").document(targetUid)
            .update("householdId", targetUid)
            .addOnSuccessListener {
                Log.d("HOGAR", "Usuario expulsado correctamente")
            }
    }

    private fun listenToEmojiConfig() {
        // Apuntamos al documento 'visuals' dentro de la colección 'config'
        db.collection("config").document("visuals").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("FIREBASE", "Error escuchando emojis", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Traemos el campo 'emojiMap' que es un Mapa en Firebase
                val map = snapshot.get("emojiMap") as? Map<String, String>
                if (map != null) {
                    dynamicEmojiMap = map
                    Log.d("FIREBASE", "Emojis actualizados desde la nube: ${map.size} cargados")
                }
            }
        }
    }

    private fun listenToProducts(hId: String) {
        db.collection("households").document(hId).collection("products")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _products.value = emptyList()
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                _products.value =
                    list.sortedWith(compareBy<Product> { it.category }.thenBy { it.name })
            }
    }

    fun addProduct(name: String, category: String, stock: Double, unit: String, minStock: Double) {
        val hid = householdId ?: return

        // Ruta nueva: households/ID/products
        val docRef = db.collection("households").document(hid).collection("products").document()

        val newProduct = Product(
            id = docRef.id,
            name = name,
            category = category,
            currentStock = stock,
            unit = unit,
            minStock = minStock,
            householdId = hid
        )

        docRef.set(newProduct).addOnSuccessListener {
            Log.d("FIREBASE", "Producto creado exitosamente en households/$hid/products")
        }.addOnFailureListener { e ->
            Log.e("FIREBASE", "Error al crear producto: ${e.message}")
        }
    }

    fun updateProduct(
        id: String, name: String, category: String, stock: Double, unit: String, minStock: Double
    ) {
        val hId = householdId ?: return // Usamos tu variable de estado

        val updatedProduct = mapOf(
            "name" to name,
            "category" to category,
            "currentStock" to stock,
            "unit" to unit,
            "minStock" to minStock,
            "householdId" to hId
        )

        db.collection("households").document(hId).collection("products").document(id)
            .update(updatedProduct)
            .addOnSuccessListener { Log.d("FIREBASE", "Producto $id actualizado en el hogar $hId") }
            .addOnFailureListener { e -> Log.e("FIREBASE", "Error al actualizar: ${e.message}") }
    }

    fun deleteMultipleProducts(productIds: Set<String>) {
        val hId = householdId ?: return

        productIds.forEach { id ->
            db.collection("households").document(hId).collection("products").document(id).delete()
                .addOnFailureListener { e ->
                    Log.e(
                        "FIREBASE", "Error al borrar $id: ${e.message}"
                    )
                }
        }
    }

    private fun listenToHouseholdCategories(hId: String) {
        db.collection("households").document(hId).collection("categories")
            .addSnapshotListener { snapshot, _ ->
                val cats = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Category::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                if (cats.isEmpty()) {
                    seedDefaultCategories(hId)
                } else {
                    _categories.value = cats.sortedWith(compareBy<Category> {
                        it.name.equals(
                            "Otros",
                            ignoreCase = true
                        )
                    }.thenBy { it.name })
                }
            }
    }

    private fun seedDefaultCategories(hId: String) {
        val batch = db.batch()
        DEFAULT_CATEGORIES.forEach { name ->
            val fixedId = name.lowercase().replace(" ", "_")
            val docRef = db.collection("households").document(hId).collection("categories")
                .document(fixedId) // <--- ID FIJO

            batch.set(docRef, Category(id = fixedId, name = name.capitalizeFirst()))
        }
        batch.commit()
    }

    fun addCategory(name: String) {
        val hId = householdId // Tu variable unificada
        Log.d("DEBUG_STOCK", "Intentando agregar categoría. ID del hogar: $hId")

        if (hId == null) {
            Log.e("DEBUG_STOCK", "ERROR: householdId es NULL. Por eso no hace nada.")
            return
        }

        val docRef = db.collection("households").document(hId).collection("categories").document()

        val newCat = Category(id = docRef.id, name = name.trim().capitalizeFirst())

        docRef.set(newCat).addOnSuccessListener {
            Log.d("DEBUG_STOCK", "¡ÉXITO! Categoría guardada en Firestore.")
        }.addOnFailureListener { e ->
            Log.e("DEBUG_STOCK", "ERROR DE FIRESTORE: ${e.message}")
        }
    }

    // Editar una categoría
    fun updateCategory(categoryId: String, newName: String) {
        val hId = householdId ?: return

        db.collection("households").document(hId).collection("categories").document(categoryId)
            .update("name", newName.trim().capitalizeFirst())
            .addOnSuccessListener { Log.d("Firestore", "Categoría editada") }
    }

    // Eliminar una categoría
    fun deleteCategory(categoryId: String) {
        val hId = householdId ?: return

        db.collection("households").document(hId).collection("categories").document(categoryId)
            .delete().addOnSuccessListener { Log.d("Firestore", "Categoría eliminada") }
    }

    fun signInWithGoogle(idToken: String, onResult: (String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener { result ->
            val uid = result.user?.uid ?: ""
            setupUserAndHousehold(uid)
            onResult(null) // Éxito
        }.addOnFailureListener { exception ->
            onResult(mapFirebaseError(exception))
        }
    }

    fun joinHousehold(shortCode: String) {
        val uid = auth.currentUser?.uid ?: return
        val codeUpper = shortCode.uppercase().trim()

        val isOwner = householdId == uid
        val hasOtherMembers = _members.value.size > 1

        viewModelScope.launch {
            // 1. Validación de Regla de Negocio
            if (isOwner && hasOtherMembers) {
                _uiEvent.emit("Sos el administrador. Para unirte a otro hogar, primero debés eliminar a todos los miembros actuales.")
                return@launch
            }

            // 2. Consulta a Firebase (usando el listener de éxito/error)
            db.collection("invitations").document(codeUpper).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val realId = doc.getString("householdId") ?: ""

                        actualizarUsuarioYHogar(uid, realId)
                    } else {
                        viewModelScope.launch { _uiEvent.emit("El código no existe o es incorrecto.") }
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch { _uiEvent.emit("Error de conexión: ${it.message}") }
                }
        }
    }

    private fun actualizarUsuarioYHogar(uid: String, realId: String) {
        db.collection("users").document(uid).update("householdId", realId)
            .addOnSuccessListener {
                this.householdId = realId
                fetchOrCreateInviteCode(realId)
                listenToProducts(realId)
                listenToHouseholdCategories(realId)

                viewModelScope.launch { _uiEvent.emit("¡Te uniste con éxito!") }
            }
    }

    fun loginWithEmail(e: String, p: String, onResult: (String?) -> Unit) {
        if (e.isBlank() || p.isBlank()) {
            onResult("Por favor, completa los campos")
            return
        }
        auth.signInWithEmailAndPassword(e.trim(), p.trim()).addOnSuccessListener {
            onResult(null)
        }.addOnFailureListener { exception ->
            // Llamamos al traductor pasándole la excepción completa
            val mensajeAmigable = mapFirebaseError(exception)
            onResult(mensajeAmigable)
        }
    }

    fun registerWithEmail(e: String, p: String, onResult: (String?) -> Unit) {
        if (e.isBlank() || p.isBlank()) {
            onResult("Por favor, completa los campos")
            return
        }

        auth.createUserWithEmailAndPassword(e.trim(), p.trim()).addOnSuccessListener { result ->
            val user = result.user
            val uid = user?.uid ?: ""

            // 1. Creamos el perfil inicial en Firestore
            val userData = hashMapOf(
                "email" to e.trim(), "householdId" to uid, "hasSeenOnboarding" to false
            )

            db.collection("users").document(uid).set(userData).addOnSuccessListener {
                user?.sendEmailVerification()?.addOnCompleteListener { task ->
                    // ¡ESTA ES LA CLAVE!
                    // Lo deslogueamos apenas se registra para que no entre por la ventana.
                    auth.signOut()

                    if (task.isSuccessful) {
                        onResult(null)
                    } else {
                        onResult("Cuenta creada, pero falló el envío del mail.")
                    }
                }
            }.addOnFailureListener { onResult("Error al crear perfil en base de datos") }

        }.addOnFailureListener { exception ->
            val mensajeAmigable = mapFirebaseError(exception)
            onResult(mensajeAmigable)
        }
    }

    private fun mapFirebaseError(exception: Exception): String {
        val errorCode = (exception as? com.google.firebase.auth.FirebaseAuthException)?.errorCode

        return when (errorCode) {
            // Capturamos todas las variantes de credenciales mal puestas
            "auth/invalid-credential", "ERROR_INVALID_CREDENTIAL", "invalid-credential" -> "El email o la contraseña son incorrectos."

            "auth/invalid-email", "ERROR_INVALID_EMAIL" -> "El formato del email no es válido."

            "auth/user-not-found", "ERROR_USER_NOT_FOUND" -> "No encontramos ninguna cuenta con ese email."

            "auth/wrong-password", "ERROR_WRONG_PASSWORD" -> "La contraseña no coincide."

            "auth/email-already-in-use", "ERROR_EMAIL_ALREADY_IN_USE" -> "Este email ya está registrado."

            "auth/weak-password", "ERROR_WEAK_PASSWORD" -> "La clave es muy corta (mínimo 6 caracteres)."

            "auth/network-request-failed" -> "Sin conexión a internet."

            // El "Atrapalotodo" amigable: Si no conocemos el error, no le mostramos el código raro
            else -> "No pudimos iniciar sesión. Verificá tus datos e intentá de nuevo."
        }
    }

    fun fetchOrCreateInviteCode(hId: String) {

        // Buscamos si este hogar ya tiene un código generado
        db.collection("invitations").whereEqualTo("householdId", hId).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    // Ya existe, lo guardamos en la variable para el Drawer
                    inviteCode = snapshot.documents[0].id
                } else {
                    // No existe, creamos uno nuevo
                    val newShortCode =
                        (1..6).map { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")
                    val data = hashMapOf(
                        "householdId" to hId, "createdAt" to com.google.firebase.Timestamp.now()
                    )
                    db.collection("invitations").document(newShortCode).set(data)
                        .addOnSuccessListener { inviteCode = newShortCode }
                }
            }
    }

    fun setupUserAndHousehold(uid: String) {
        val userRef = db.collection("users").document(uid)
        val currentUser = auth.currentUser // Obtenemos el usuario de Auth (Google o Email)

        userRef.get().addOnSuccessListener { doc ->
            // --- LÓGICA DE NOMBRE (Tu syncUserProfile) ---
            val nameFromAuth = currentUser?.displayName
            val namePlaceholder =
                currentUser?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
                    ?: "Usuario Master"

            // El nombre final será: El de Google si existe, sino el placeholder del email
            val finalName = if (!nameFromAuth.isNullOrEmpty()) nameFromAuth else namePlaceholder
            val finalPhoto = currentUser?.photoUrl?.toString() ?: ""

            if (doc.exists()) {
                // EL USUARIO YA EXISTE: Actualizamos por si cambió algo en su perfil
                val hId = doc.getString("householdId") ?: uid
                this.householdId = hId

                val updates = hashMapOf(
                    "displayName" to (doc.getString("displayName")
                        ?: finalName), // Mantenemos el de Firestore si ya tiene uno
                    "photoUrl" to finalPhoto,
                    "email" to (currentUser?.email ?: "")
                )
                userRef.update(updates as Map<String, Any>)

                // Lanzamos todo lo demás
                fetchOrCreateInviteCode(hId)
                listenToProducts(hId)
                listenToHouseholdCategories(hId)
                listenToMembers()

                hasSeenOnboarding = doc.getBoolean("has_seen_onboarding") ?: false
            } else {
                // USUARIO NUEVO: Creamos el documento desde cero
                val initialData = hashMapOf(
                    "uid" to uid,
                    "displayName" to finalName,
                    "email" to (currentUser?.email ?: ""),
                    "photoUrl" to finalPhoto,
                    "householdId" to uid, // Su propia casa por defecto
                    "createdAt" to com.google.firebase.Timestamp.now(),
                    "has_seen_onboarding" to false
                )

                userRef.set(initialData).addOnSuccessListener {
                    this.householdId = uid
                    fetchOrCreateInviteCode(uid)
                    listenToProducts(uid)
                    listenToHouseholdCategories(uid)
                    listenToMembers()
                }
            }
        }
    }

    fun markOnboardingAsSeen(uid: String) {
        db.collection("users").document(uid).update("has_seen_onboarding", true)
        hasSeenOnboarding = true
    }

    fun purchaseProduct(product: Product, cantidadComprada: Double) {
        val hId = householdId ?: return

        // El nuevo stock es lo que ya tenías + lo que acabás de comprar
        val nuevoStock = product.currentStock + cantidadComprada

        db.collection("households").document(hId).collection("products").document(product.id)
            .update("currentStock", nuevoStock)
            .addOnSuccessListener { Log.d("FIREBASE", "Compra registrada para ${product.name}") }
    }

    fun quickUpdateStock(product: Product, isAdding: Boolean) {
        val hId = householdId ?: return

        // Definimos el salto según la unidad
        val step = when (product.unit.lowercase().trim()) {
            "g", "gr", "gramos" -> 10.0
            "ml" -> 100.0
            "kg", "l", "litros" -> 0.5
            else -> 1.0 // Para "unidades", "packs", "sobres", etc.
        }

        val delta = if (isAdding) step else -step
        val finalStock = (product.currentStock + delta).coerceAtLeast(0.0)

        db.collection("households").document(hId).collection("products").document(product.id)
            .update("currentStock", finalStock)
    }

    fun resetStock(product: Product) {
        val hId = householdId ?: return

        db.collection("households").document(hId).collection("products").document(product.id)
            .update("currentStock", 0.0)
    }

    fun updateStockDirectly(product: Product, newStock: Double) {
        val hId = householdId ?: return

        db.collection("households").document(hId).collection("products").document(product.id)
            .update("currentStock", newStock.coerceAtLeast(0.0))
    }

    fun resetPassword(email: String, onResult: (String?) -> Unit) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(null) // Todo bien
            } else {
                // Manejo de errores amigable
                val errorMsg =
                    when ((task.exception as? com.google.firebase.auth.FirebaseAuthException)?.errorCode) {
                        "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este email."
                        "ERROR_INVALID_EMAIL" -> "El formato del email no es válido."
                        else -> task.exception?.localizedMessage ?: "Error al enviar el correo."
                    }
                onResult(errorMsg)
            }
        }
    }

    fun leaveHousehold() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .update("householdId", uid) // Vuelve a ser su propia casa vacía
            .addOnSuccessListener {
                this.householdId = uid
                // Recargamos los listeners para que vea su inventario (ahora vacío)
                listenToProducts(uid)
                listenToHouseholdCategories(uid)
                listenToMembers()
            }
    }

}