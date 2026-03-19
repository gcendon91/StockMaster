package com.gcendon.stockmaster.ui.viewmodel

import androidx.lifecycle.ViewModel
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

    init {
        listenToProducts()
    }

    private fun listenToProducts() {
        // Nos "suscribimos" a la colección "products" de Firebase
        db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null) {
                    // Convertimos los documentos de Firebase a nuestra lista de objetos Product
                    val list = snapshot.toObjects(Product::class.java)
                    _products.value = list
                }
            }
    }

    fun addProduct(name: String, category: String, stock: Double, unit: String) {
        val newProduct = Product(
            name = name,
            category = category,
            currentStock = stock,
            minStock = 1.0,
            unit = unit
        )
        db.collection("products").add(newProduct)
    }
}