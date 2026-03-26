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

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    var householdId by mutableStateOf<String?>(null)
        private set

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    val shoppingList: StateFlow<List<Product>> = products.map { lista ->
        lista.filter { it.currentStock < it.minStock }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        auth.currentUser?.uid?.let { uid ->
            setupUserAndHousehold(uid)
        }
        listenToCategories()
    }

    private fun setupUserAndHousehold(uid: String) {
        val userRef = db.collection("users").document(uid)

        userRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                // Usuario viejo: usamos su ID de casa guardado
                householdId = doc.getString("householdId")
                listenToProducts() // Ahora que tenemos el ID, cargamos los productos
            } else {
                // Usuario nuevo: su UID será su primer código de casa
                val initialData = mapOf("householdId" to uid)
                userRef.set(initialData).addOnSuccessListener {
                    householdId = uid
                    listenToProducts()
                }
            }
        }
    }

    private fun listenToProducts() {
        val hid = householdId ?: return

        db.collection("products")
            .whereEqualTo("householdId", hid)
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

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener { result ->
            val uid = result.user?.uid
            if (uid != null) {
                setupUserAndHousehold(uid) // <--- Vinculamos al usuario apenas entra
            }
            Log.d("AUTH", "Login exitoso")
        }.addOnFailureListener { e ->
            Log.e("AUTH", "Error de login: ${e.message}")
        }
    }

    fun joinHousehold(newId: String, onResult: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        // 1. PRIMERO VALIDAMOS: ¿Existe algún usuario que use este ID de hogar?
        db.collection("users")
            .whereEqualTo("householdId", newId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    // 2. SI EXISTE: Recién ahí actualizamos nuestro perfil
                    db.collection("users").document(uid)
                        .update("householdId", newId)
                        .addOnSuccessListener {
                            this.householdId = newId
                            listenToProducts()
                            onResult(true, "¡Te uniste con éxito!")
                        }
                } else {
                    // 3. NO EXISTE: Avisamos que el código es cualquiera
                    onResult(false, "El código ingresado no existe.")
                }
            }
            .addOnFailureListener {
                onResult(false, "Error de conexión: ${it.message}")
            }
    }

    fun loginWithEmail(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) return
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { Log.d("AUTH", "Login OK") }
            .addOnFailureListener { Log.e("AUTH", "Login Error: ${it.message}") }
    }

    fun registerWithEmail(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) return
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { Log.d("AUTH", "Registro OK") }
            .addOnFailureListener { Log.e("AUTH", "Registro Error: ${it.message}") }
    }

    fun loginWithGoogle() {
        Log.d("AUTH", "Google Login Clicked")
    }
}