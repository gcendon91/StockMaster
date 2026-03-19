package com.gcendon.stockmaster.data

/**
 * El modelo ahora es compatible con Firestore.
 * Firestore requiere que las propiedades tengan valores por defecto.
 */
data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "General",
    val currentStock: Double = 0.0,
    val minStock: Double = 1.0,
    val idealStock: Double = 1.0,
    val unit: String = "unid",
    val bought: Boolean = false
)