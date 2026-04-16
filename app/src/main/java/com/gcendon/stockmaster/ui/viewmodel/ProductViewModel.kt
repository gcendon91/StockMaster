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

    init {
        auth.currentUser?.uid?.let { uid ->
            setupUserAndHousehold(uid)
        }
        listenToCategories()
        listenToEmojiConfig()
    }

    private fun listenToEmojiConfig() {
        // Apuntamos al documento 'visuals' dentro de la colección 'config'
        db.collection("config").document("visuals")
            .addSnapshotListener { snapshot, e ->
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

    private fun listenToProducts() {
        val hid = householdId ?: return

        db.collection("products").whereEqualTo("householdId", hid)
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

    private fun listenToCategories() {
        db.collection("categories").addSnapshotListener { snapshot, _ ->
            val cats = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Category::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            if (cats.isEmpty()) {
                // Si no hay nada, sembramos las básicas
                seedDefaultCategories()
            } else {
                _categories.value = cats
            }
        }
    }

    private fun seedDefaultCategories() {
        val defaults = listOf("Almacén", "Lácteos", "Carnicería", "Limpieza", "Verdulería", "Otros")
        defaults.forEach { name ->
            addCategory(name)
        }
    }

    fun addCategory(name: String) {
        if (name.isNotBlank()) {
            val newCat = Category(name = name)
            db.collection("categories").add(newCat)
        }
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

        // 1. Buscamos el documento que se llama como el código (ej: "A8J3K2")
        db.collection("invitations").document(codeUpper).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                // 2. Si existe, sacamos el ID real de la casa
                val realId = doc.getString("householdId") ?: ""

                // 3. Actualizamos el perfil del usuario con ese ID real
                db.collection("users").document(uid).update("householdId", realId)
                    .addOnSuccessListener {
                        this.householdId = realId
                        fetchOrCreateInviteCode() // Actualizamos el código que vemos
                        listenToProducts()        // Cargamos los productos nuevos
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
            val uid = result.user?.uid ?: ""
            // Creamos el perfil inicial
            val userData = hashMapOf(
                "email" to e.trim(), "householdId" to uid // Su propia casa por defecto
            )
            db.collection("users").document(uid).set(userData)
                .addOnSuccessListener { onResult(null) }
                .addOnFailureListener { onResult("Error al crear perfil en base de datos") }
        }.addOnFailureListener { exception ->
            // Llamamos al traductor pasándole la excepción completa
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

    fun fetchOrCreateInviteCode() {
        val hid = householdId ?: return

        // Buscamos si este hogar ya tiene un código generado
        db.collection("invitations").whereEqualTo("householdId", hid).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    // Ya existe, lo guardamos en la variable para el Drawer
                    inviteCode = snapshot.documents[0].id
                } else {
                    // No existe, creamos uno nuevo
                    val newShortCode =
                        (1..6).map { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")
                    val data = hashMapOf(
                        "householdId" to hid, "createdAt" to com.google.firebase.Timestamp.now()
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
                // Usuario ya registrado: cargamos su casa
                householdId = doc.getString("householdId")
                fetchOrCreateInviteCode() // Buscamos su código de 6 letras
                listenToProducts()
            } else {
                // Usuario nuevo (Email o Google): le creamos su casa inicial
                val email = auth.currentUser?.email
                val initialData = mapOf(
                    "email" to email,
                    "householdId" to uid,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                userRef.set(initialData).addOnSuccessListener {
                    householdId = uid
                    fetchOrCreateInviteCode()
                    listenToProducts()
                }
            }
        }
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

}