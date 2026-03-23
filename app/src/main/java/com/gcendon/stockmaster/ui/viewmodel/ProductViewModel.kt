package com.gcendon.stockmaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.gcendon.stockmaster.data.Category
import com.gcendon.stockmaster.data.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel : ViewModel() {
    private val db = Firebase.firestore // Referencia a la base de datos

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        listenToProducts()
        listenToCategories()
    }

    private fun listenToProducts() {
        db.collection("products").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener

            val list = snapshot?.documents?.mapNotNull { doc ->
                //Convertimos el documento a objeto Product
                val producto = doc.toObject(Product::class.java)

                // Le pedimos a Firebase el ID real del documento (doc.id)
                // y se lo pegamos a nuestro objeto
                producto?.copy(id = doc.id)
            } ?: emptyList()

            // Ordenamos: 1º por Categoría, 2º por Nombre
            val listaOrdenada = list.sortedWith(
                compareBy<Product> { it.category }.thenBy { it.name }
            )

            _products.value = listaOrdenada
        }
    }

    fun addProduct(name: String, category: String, stock: Double, unit: String, ideal: Double) {
        val newProduct = Product(
            name = name,
            category = category,
            currentStock = stock,
            idealStock = ideal,
            minStock = 1.0,
            unit = unit
        )
        db.collection("products").add(newProduct)
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
            db.collection("categories").document(category.id)
                .update("name", newName)
        }
    }

    // Eliminar una categoría
    fun deleteCategory(categoryId: String) {
        db.collection("categories").document(categoryId).delete()
        // Nota: Aquí podrías agregar lógica para que los productos
        // que tenían esta categoría pasen a "Sin Categoría"
    }
}