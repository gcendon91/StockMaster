package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ProductCard

@Composable
fun HomeScreen(innerPadding: PaddingValues) {
    // 1. Datos de prueba (Mock Data) para ver cómo queda
    val demoProducts = listOf(
        Product(name = "Aceite de Oliva", category = "Almacén", currentStock = 0.5f, minStock = 1f),
        Product(name = "Leche Entera", category = "Lácteos", currentStock = 3f, minStock = 1f),
        Product(name = "Arroz Integral", category = "Almacén", currentStock = 1f, minStock = 1f),
        Product(name = "Detergente", category = "Limpieza", currentStock = 0f, minStock = 1f)
    )

    // 2. La lista inteligente
    LazyColumn(
        contentPadding = innerPadding // Respeta los márgenes del Scaffold
    ) {
        items(demoProducts) { product ->
            ProductCard(product = product)
        }
    }
}