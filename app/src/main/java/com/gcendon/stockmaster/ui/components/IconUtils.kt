package com.gcendon.stockmaster.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Utilidad arquitectónica para mapear categorías a iconos visuales.
 * Al estar en un archivo separado, podés actualizar los iconos de toda la app
 * desde un solo lugar.
 */
object IconUtils {
    fun getProductIcon(name: String, category: String): ImageVector {
        val nameLower = name.lowercase()

        return when {
            // LÁCTEOS: No hay cartón de leche, usamos la gota o el huevo
            nameLower.contains("leche") || nameLower.contains("yogur") -> Icons.Rounded.WaterDrop

            // PANADERÍA: BakeryDining es un croissant/pan
            nameLower.contains("pan") || nameLower.contains("factura") -> Icons.Rounded.BakeryDining

            // CARNE: KebabDining son como brochetas, Restaurant son cubiertos
            nameLower.contains("carne") || nameLower.contains("pollo") -> Icons.Rounded.KebabDining

            // FRUTA/VERDURA: Eco es una hojita. No hay manzana en la librería.
            nameLower.contains("manzana") || nameLower.contains("fruta") || nameLower.contains("verdura") -> Icons.Rounded.Eco

            // BEBIDAS: LocalDrink es un vaso con sorbete
            nameLower.contains("cerveza") || nameLower.contains("vino") || nameLower.contains("soda") -> Icons.Rounded.LocalDrink

            // PASTAS / ARROZ: DinnerDining es un plato con comida
            nameLower.contains("fideo") || nameLower.contains("arroz") -> Icons.Rounded.DinnerDining

            // LIMPIEZA: CleaningServices es un spray
            nameLower.contains("jabon") || nameLower.contains("detergente") -> Icons.Rounded.CleaningServices

            else -> getCategoryBackupIcon(category)
        }
    }

    private fun getCategoryBackupIcon(category: String): ImageVector {
        return when (category) {
            "Lácteos" -> Icons.Rounded.Egg // El huevo es lo más "lácteo" que hay
            "Carnicería" -> Icons.Rounded.Restaurant
            "Verdulería" -> Icons.Rounded.Eco
            "Limpieza" -> Icons.Rounded.AutoAwesome // Da sensación de brillo/limpio
            else -> Icons.Rounded.Inventory2
        }
    }
}