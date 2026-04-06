package com.gcendon.stockmaster.ui.utils

object IconUtils {
    fun getProductEmoji(name: String, category: String): String {
        val n = name.lowercase()

        return when {
            // --- 1. CASOS ESPECÍFICOS / LARGOS (Para evitar choques) ---
            // Ponemos "salsa" antes que "sal"
            n.contains("salsa") || n.contains("ketchup") || n.contains("mayonesa") || n.contains("mostaza") -> "🥫"

            // Ponemos "estropajo" antes que "ajo"
            n.contains("estropajo") || n.contains("esponja") || n.contains("trapo") -> "🧽"

            // Ponemos "café en granos" antes que "café"
            n.contains("café en granos") || n.contains("cafe en granos") -> "🫘"

            // --- 2. CONDIMENTOS Y ESPECIAS ---
            n.contains("ajo") -> "🧄" // Ahora solo entra acá si no es estropajo
            n.contains("sal") -> "🧂" // Ahora solo entra acá si no es salsa
            n.contains("oliva") -> "🫒"
            n.contains("aceite") -> "🍶"
            n.contains("vinagre") || n.contains("aceto") -> "🏺"
            n.contains("azucar") || n.contains("azúcar") -> "🧂"
            n.contains("miel") -> "🍯"
            n.contains("caldo") || n.contains("bicarbonato") -> "📦"

            // Especias
            n.contains("ají molido") || n.contains("aji molido") || n.contains("pimenton") || n.contains(
                "pimentón"
            ) || n.contains("pimienta") || n.contains("aji") -> "🌶️"

            n.contains("albahaca") || n.contains("orégano") || n.contains("oregano") || n.contains("provenzal") || n.contains(
                "hierba"
            ) -> "🌿"
            n.contains("comino") || n.contains("curcuma") || n.contains("curry") || n.contains("canela") -> "🍂"

            // --- 3. CARNICERÍA Y PROTEÍNAS ---
            n.contains("pollo") || n.contains("pechuga") -> "🍗"
            n.contains("cerdo") -> "🐷"
            n.contains("cuadril") || n.contains("milanesa") || n.contains("carne") || n.contains("higado") || n.contains(
                "hígado"
            ) -> "🥩"

            n.contains("jamon") || n.contains("jamón") -> "🥓"
            n.contains("pescado") -> "🐟"
            n.contains("huevo") -> "🥚"

            // --- 4. LÁCTEOS ---
            n.contains("leche") -> "🥛"
            n.contains("yogurt") || n.contains("yogur") -> "🥤"
            n.contains("queso") || n.contains("muzzarella") -> "🧀"
            n.contains("manteca") -> "🧈"

            // --- 5. ALMACÉN / SECOS ---
            n.contains("fideo") || n.contains("pasta") -> "🍝"
            n.contains("arroz") -> "🍚"
            n.contains("harina") || n.contains("avena") || n.contains("levadura") -> "🌾"
            n.contains("pan") -> "🍞"
            n.contains("galletita") -> "🍪"
            n.contains("lenteja") -> "🫘"
            n.contains("garbanzo") -> "🍲"
            n.contains("maní") || n.contains("almendra") || n.contains("frutos secos") || n.contains(
                "semillas"
            ) -> "🥜"

            n.contains("cacao") || n.contains("chocolate") -> "🍫"
            n.contains("cafe") || n.contains("café") -> "☕"
            n.contains("té") || n.contains("te") -> "🍵"
            n.contains("yerba") -> "🧉"
            n.contains("maiz") || n.contains("pisingallo") -> "🍿"
            n.contains("choclo") -> "🌽"

            // --- 6. VERDULERÍA ---
            n.contains("papa") || n.contains("batata") -> "🥔"
            n.contains("cebolla") -> "🧅"
            n.contains("morron") || n.contains("morrón") -> "🫑"
            n.contains("tomate") -> "🍅"
            n.contains("limon") || n.contains("limón") -> "🍋"
            n.contains("banana") -> "🍌"
            n.contains("manzana") -> "🍎"
            n.contains("zanahoria") -> "🥕"
            n.contains("lechuga") || n.contains("acelga") || n.contains("hoja") -> "🥬"

            // --- 7. LIMPIEZA E HIGIENE ---
            n.contains("detergente") || n.contains("jabon liquido") -> "🧼"
            n.contains("lavandina") || n.contains("limpiador") || n.contains("cif") -> "✨"
            n.contains("papel higiénico") || n.contains("higiénico") || n.contains("rollo") -> "🧻"
            n.contains("shampoo") || n.contains("acondicionador") -> "🚿"
            n.contains("dentifrico") || n.contains("dientes") -> "🪥"
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