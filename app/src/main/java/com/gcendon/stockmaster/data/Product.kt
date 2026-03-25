package com.gcendon.stockmaster.data

/**
 * El modelo ahora es compatible con Firestore.
 * Firestore requiere que las propiedades tengan valores por defecto.
 */
data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val currentStock: Double = 0.0,
    val unit: String = "",
    val minStock: Double = 0.0,
    val householdId: String = ""
)