package com.gcendon.stockmaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.gcendon.stockmaster.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel : ViewModel() {

    // Esta es la "fuente de verdad" de nuestros datos.
    // Usamos StateFlow porque es reactivo: si los datos cambian, la UI se entera sola.
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        // Por ahora, cargamos datos de prueba aquí (Mock Data)
        loadMockProducts()
    }

    private fun loadMockProducts() {
        _products.value = listOf(
            Product(name = "Aceite de Oliva", category = "Almacén", currentStock = 0.5f, minStock = 1f),
            Product(name = "Leche Entera", category = "Lácteos", currentStock = 3f, minStock = 1f),
            Product(name = "Detergente", category = "Limpieza", currentStock = 0f, minStock = 1f)
        )
    }
}