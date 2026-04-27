package com.gcendon.stockmaster.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gcendon.stockmaster.data.Category
import com.gcendon.stockmaster.data.Product
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class ProductViewModel : ViewModel() {
    private val db = Firebase.firestore // Referencia a la base de datos
    private val auth = FirebaseAuth.getInstance()

    private var currentHouseholdId: String? = null

    var dynamicEmojiMap by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    var householdId by mutableStateOf<String?>(null)
        private set

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    val shoppingList: StateFlow<List<Product>> = products.map { lista ->
        lista.filter { it.currentStock < it.minStock }
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
        "Otros",
        "Pescadería",
        "Verdulería"
    )

    fun String.capitalizeFirst(): String = this.lowercase().replaceFirstChar { it.uppercase() }

    init {
        auth.currentUser?.uid?.let { uid ->
            setupUserAndHousehold(uid)
        }
        listenToEmojiConfig()
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
                if (e != null) return@addSnapshotListener

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                val listaOrdenada =
                    list.sortedWith(compareBy<Product> { it.category }.thenBy { it.name })
                _products.value = listaOrdenada
            }
    }

    fun addProduct(name: String, category: String, stock: Double, unit: String, minStock: Double) {
        val hid = householdId ?: return

        val newProduct = Product(
            name = name,
            category = category,
            currentStock = stock,
            unit = unit,
            minStock = minStock,
            householdId = hid
        )

        db.collection("products").add(newProduct)
            .addOnSuccessListener { Log.d("FIREBASE", "Producto creado en el hogar $hid") }
    }

    fun updateProduct(
        id: String, name: String, category: String, stock: Double, unit: String, minStock: Double
    ) {
        val hid = householdId ?: return

        val updatedProduct = mapOf(
            "name" to name,
            "category" to category,
            "currentStock" to stock,
            "unit" to unit,
            "minStock" to minStock,
            "householdId" to hid // Lo mantenemos vinculado al mismo hogar
        )

        db.collection("products").document(id).update(updatedProduct)
            .addOnSuccessListener { Log.d("FIREBASE", "Producto $id actualizado") }
    }

    fun deleteMultipleProducts(productIds: Set<String>) {
        productIds.forEach { id ->
            db.collection("products").document(id).delete()
                .addOnFailureListener { e -> println("Error al borrar $id: $e") }
        }
    }

    private fun listenToHouseholdCategories(hId: String) {
        db.collection("households").document(hId)
            .collection("categories")
            .addSnapshotListener { snapshot, _ ->
                val cats = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Category::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                if (cats.isEmpty()) {
                    seedDefaultCategories(hId)
                } else {
                    _categories.value = cats.sortedBy { it.name }
                }
            }
    }

    private fun seedDefaultCategories(hId: String) {
        val batch = db.batch()
        DEFAULT_CATEGORIES.forEach { name ->
            val docRef =
                db.collection("households").document(hId).collection("categories").document()
            // Guardamos con el formato correcto
            batch.set(docRef, Category(id = docRef.id, name = name.capitalizeFirst()))
        }
        batch.commit()
    }

    fun addCategory(name: String) {
        val hId = currentHouseholdId ?: return
        val docRef = db.collection("households").document(hId).collection("categories").document()

        // Aquí también forzamos el formato si el usuario escribe "panaderia" -> "Panaderia"
        docRef.set(Category(id = docRef.id, name = name.trim().capitalizeFirst()))
    }

    // Editar una categoría
    fun updateCategory(category: Category, newName: String) {
        if (newName.isNotBlank()) {
            db.collection("categories").document(category.id).update("name", newName)
        }
    }

    // Eliminar una categoría
    fun deleteCategory(categoryId: String) {
        db.collection("categories").document(categoryId).delete()
        // Nota: Aquí podrías agregar lógica para que los productos
        // que tenían esta categoría pasen a "Sin Categoría"
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

    fun joinHousehold(shortCode: String, onResult: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val codeUpper = shortCode.uppercase().trim()

        db.collection("invitations").document(codeUpper).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val realId = doc.getString("householdId") ?: ""

                db.collection("users").document(uid).update("householdId", realId)
                    .addOnSuccessListener {
                        this.householdId = realId

                        fetchOrCreateInviteCode(realId)
                        listenToProducts(realId)
                        listenToHouseholdCategories(realId) //

                        onResult(true, "¡Te uniste con éxito!")
                    }
            } else {
                onResult(false, "El código no existe o es incorrecto.")
            }
        }.addOnFailureListener {
            onResult(false, "Error de conexión: ${it.message}")
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
                        "householdId" to hId,
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )
                    db.collection("invitations").document(newShortCode).set(data)
                        .addOnSuccessListener { inviteCode = newShortCode }
                }
            }
    }

    fun setupUserAndHousehold(uid: String) {
        val userRef = db.collection("users").document(uid)

        userRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                // 1. Obtenemos el ID y lo guardamos en la variable de la clase
                val hId = doc.getString("householdId") ?: uid
                currentHouseholdId = hId

                // 2. Pasamos 'hId' a todas las funciones que lo necesitan
                fetchOrCreateInviteCode(hId) // Asegurate que esta reciba el String
                listenToProducts(hId)        // Pasamos el ID
                listenToHouseholdCategories(hId) // <--- AGREGAMOS ESTA

                hasSeenOnboarding = doc.getBoolean("has_seen_onboarding") ?: false
            } else {
                // Usuario nuevo: creamos su casa inicial
                val email = auth.currentUser?.email
                val initialData = mapOf(
                    "email" to email,
                    "householdId" to uid,
                    "createdAt" to com.google.firebase.Timestamp.now(),
                    "has_seen_onboarding" to false
                )
                userRef.set(initialData).addOnSuccessListener {
                    currentHouseholdId = uid
                    fetchOrCreateInviteCode(uid)
                    listenToProducts(uid)
                    listenToHouseholdCategories(uid) // <--- TAMBIÉN AQUÍ
                }
            }
        }
    }

    fun markOnboardingAsSeen(uid: String) {
        db.collection("users").document(uid).update("has_seen_onboarding", true)
        hasSeenOnboarding = true
    }

    fun purchaseProduct(product: Product, cantidadComprada: Double) {
        auth.currentUser?.uid ?: return

        // El nuevo stock es lo que ya tenías + lo que acabás de comprar
        val nuevoStock = product.currentStock + cantidadComprada

        db.collection("products").document(product.id).update("currentStock", nuevoStock)
            .addOnSuccessListener {
                // Se actualiza solo en la lista
            }
    }

    fun quickUpdateStock(product: Product, isAdding: Boolean) {
        // Definimos el salto según la unidad
        val step = when (product.unit.lowercase().trim()) {
            "g", "gr", "gramos" -> 10.0
            "ml" -> 100.0
            "kg", "l", "litros" -> 0.5
            else -> 1.0 // Para "unidades", "packs", "sobres", etc.
        }

        val delta = if (isAdding) step else -step
        val finalStock = (product.currentStock + delta).coerceAtLeast(0.0)

        db.collection("products").document(product.id).update("currentStock", finalStock)
    }

    fun resetStock(product: Product) {
        db.collection("products").document(product.id).update("currentStock", 0.0)
    }

    fun updateStockDirectly(product: Product, newStock: Double) {
        db.collection("products").document(product.id)
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

}