package com.gcendon.stockmaster.ui.utils

import androidx.compose.runtime.Composable

object IconUtils {
    fun getProductEmoji(name: String, category: String): String {
        val n = name.lowercase()

        return when {
            // --- PRIORIDAD: PALABRAS CLAVE ESPECÍFICAS (Correcciones UX) ---

            // Aceites y Condimentos
            n.contains("oliva") -> "🫒"
            n.contains("aceite") -> "🍶"
            n.contains("vinagre") || n.contains("aceto") -> "🏺"
            n.contains("sal") -> "🧂"
            n.contains("azucar") -> "🧂" // Cambiado: de caramelo a ingrediente en polvo
            n.contains("miel") -> "🍯"
            n.contains("mayonesa") || n.contains("mostaza") || n.contains("salsa") -> "🥫"
            n.contains("caldo") || n.contains("horno") || n.contains("bicarbonato") -> "📦"

            // Especias
            n.contains("ajo") -> "🧄"
            n.contains("aji") || n.contains("pimenton") || n.contains("pimienta") -> "🌶️"
            n.contains("albahaca") || n.contains("oregano") || n.contains("provenzal") || n.contains("hierba") -> "🌿"
            n.contains("comino") || n.contains("curcuma") || n.contains("curry") || n.contains("canela") -> "🍂"

            // Carnicería y Proteínas
            n.contains("cerdo") -> "🐷"
            n.contains("pollo") || n.contains("pechuga") -> "🍗"
            n.contains("cuadril") || n.contains("milanesa") || n.contains("carne") -> "🥩"
            n.contains("jamon") -> "🥓"
            n.contains("pescado") -> "🐟"
            n.contains("huevo") -> "🥚"

            // Lácteos y derivados
            n.contains("leche") -> "🥛"
            n.contains("yogurt") || n.contains("yogur") -> "🥤"
            n.contains("queso") || n.contains("muzzarella") -> "🧀"
            n.contains("manteca") -> "🧈"

            // Almacén / Secos
            n.contains("fideo") || n.contains("pasta") -> "🍝"
            n.contains("arroz") -> "🍚"
            n.contains("harina") || n.contains("avena") || n.contains("levadura") -> "🌾"
            n.contains("pan") -> "🍞"
            n.contains("galletita") -> "🍪"
            n.contains("lenteja") || n.contains("garbanzo") -> "🍲"
            n.contains("mani") || n.contains("almendra") || n.contains("fruto seco") || n.contains("semilla") -> "🥜"
            n.contains("cacao") || n.contains("chocolate") -> "🍫" // Cambiado: de taza a barra
            n.contains("cafe") -> "☕"
            n.contains("te") -> "🍵"
            n.contains("yerba") -> "🧉"
            n.contains("maiz") || n.contains("pisingallo") || n.contains("choclo") -> "🍿"

            // Verdulería
            n.contains("papa") || n.contains("batata") || n.contains("boniato") -> "🥔"
            n.contains("cebolla") -> "🧅"
            n.contains("morron") -> "🫑"
            n.contains("tomate") -> "🍅"
            n.contains("limon") -> "🍋"
            n.contains("manzana") -> "🍎"
            n.contains("banana") -> "🍌"
            n.contains("naranja") -> "🍊"
            n.contains("zanahoria") -> "🥕"
            n.contains("hoja") || n.contains("lechuga") || n.contains("acelga") -> "🥬"
            n.contains("fruta") -> "🍎"

            // Limpieza del Hogar
            n.contains("detergente") || n.contains("jabon liquido") -> "🧼"
            n.contains("suavizante") || n.contains("vanish") || n.contains("camellito") -> "🧺"
            n.contains("lavandina") || n.contains("limpiador") || n.contains("cif") || n.contains("lysoform") -> "✨"
            n.contains("antigrasa") || n.contains("blem") || n.contains("limpiavidrios") -> "🪟"
            n.contains("trapo") || n.contains("ballerina") || n.contains("estropajo") || n.contains("esponja") -> "🧽"
            n.contains("bolsa") -> "🛍️"
            n.contains("film") || n.contains("aluminio") || n.contains("separador") -> "🎞️"

            // Higiene Personal
            n.contains("shampoo") || n.contains("acondicionador") || n.contains("enjuague") -> "🚿"
            n.contains("jabon de tocador") || n.contains("glicerina") -> "🧼"
            n.contains("dentifrico") || n.contains("cepillo") || n.contains("dentastix") -> "🪥"
            n.contains("bucal") -> "🦷"
            n.contains("desodorante") -> "☁️"
            n.contains("papel higienico") || n.contains("higienico") || n.contains("rollo") -> "🧻"
            n.contains("pañuelito") || n.contains("algodon") || n.contains("hisopo") -> "☁️"
            n.contains("toallita") || n.contains("protector") || n.contains("tampon") -> "🌸"

            // Otros / Mascotas
            n.contains("dentastix") -> "🐕"

            // --- RESPALDO POR CATEGORÍA ---
            else -> getCategoryBackupEmoji(category)
        }
    }

    private fun getCategoryBackupEmoji(category: String): String {
        return when (category) {
            "Lácteos" -> "🐄"
            "Carnicería" -> "🥩"
            "Verdulería" -> "🥦"
            "Almacén" -> "🛍️"
            "Limpieza" -> "✨"
            "Higiene" -> "🧼"
            else -> "📦"
        }
    }
}