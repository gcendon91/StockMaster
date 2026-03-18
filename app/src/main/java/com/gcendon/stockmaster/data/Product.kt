package com.gcendon.stockmaster.data

/**
 * Representa un artículo en nuestra despensa o lista de compras.
 * Usamos valores por defecto para que sea compatible con Firebase más adelante.
 */
data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "General", // Ej: Lácteos, Almacén, Limpieza
    val currentStock: Float = 0f,    // Usamos Float por si hay "0.5 kg"
    val minStock: Float = 1f,        // El umbral para ir al súper
    val unit: String = "unid",       // kg, ml, unid, etc.
    val isBought: Boolean = false    // Estado para la lista de compras
)