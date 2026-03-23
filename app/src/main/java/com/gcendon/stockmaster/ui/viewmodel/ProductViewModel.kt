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
}