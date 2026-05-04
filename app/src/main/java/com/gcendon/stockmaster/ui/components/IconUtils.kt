package com.gcendon.stockmaster.ui.utils

object IconUtils {

    fun getProductEmoji(
        name: String,
        category: String,
        dynamicMap: Map<String, String>
    ): String {
        val nameLower = name.lowercase().trim()
        val catLower = category.lowercase().trim()

        // 1. Buscamos coincidencia exacta por nombre en la nube
        if (dynamicMap.containsKey(nameLower)) return dynamicMap[nameLower]!!

        // 2. Buscamos coincidencia por categoría en la nube
        if (dynamicMap.containsKey(catLower)) return dynamicMap[catLower]!!

        // 3. Plan de rescate: Lógica local (contains)
        return getLocalEmoji(nameLower, category)
    }

    private fun getLocalEmoji(name: String, category: String): String {
        val n = name // ya viene en lowercase de la función de arriba
        return when {
            n.contains("salsa") || n.contains("ketchup") || n.contains("mayonesa") || n.contains("mostaza") || n.contains(
                "barbacoa"
            ) -> "🥫"
            n.contains("salchicha") -> "🌭"
            n.contains("estropajo") || n.contains("esponja") || n.contains("trapo") -> "🧽"
            n.contains("café en granos") || n.contains("cafe en granos") -> "🫘"
            n.contains("dientes") || n.contains("cepillos de dientes") -> "🪥"
            n.contains("desodorante") -> "💨"
            n.contains("guantes de cocina") -> "🧤"
            n.contains("tomate") -> "🍅"
            n.contains("detergente") -> "🧼"
            n.contains("protectores diarios") -> "♀️"
            n.contains("ajo") -> "🧄"
            n.contains("sal") -> "🧂"
            n.contains("oliva") -> "🫒"
            n.contains("aceite") -> "🍶"
            n.contains("vinagre") || n.contains("aceto") -> "🏺"
            n.contains("azucar") || n.contains("azúcar") -> "🧂"
            n.contains("miel") -> "🍯"
            n.contains("ají molido") || n.contains("aji molido") || n.contains("pimenton") || n.contains(
                "pimentón"
            ) || n.contains("pimienta") || n.contains("aji") -> "🌶️"

            n.contains("albahaca") || n.contains("orégano") || n.contains("oregano") || n.contains("provenzal") || n.contains(
                "hierba"
            ) -> "🌿"
            n.contains("comino") || n.contains("cúrcuma") || n.contains("curry") || n.contains("canela") -> "🍂"
            n.contains("pollo") || n.contains("pechuga") -> "🍗"
            n.contains("cerdo") -> "🐷"
            n.contains("cuadril") || n.contains("milanesa") || n.contains("carne") || n.contains("higado") || n.contains(
                "hígado"
            ) -> "🥩"
            n.contains("jamon") || n.contains("jamón") -> "🥓"
            n.contains("pescado") -> "🐟"
            n.contains("huevo") -> "🥚"
            n.contains("leche") -> "🥛"
            n.contains("yogurt") || n.contains("yogur") -> "🥤"
            n.contains("queso") || n.contains("muzzarella") -> "🧀"
            n.contains("manteca") -> "🧈"
            n.contains("fideo") || n.contains("pasta") -> "🍝"
            n.contains("arroz") -> "🍚"
            n.contains("harina") || n.contains("avena") || n.contains("levadura") -> "🌾"
            n.contains("pan") -> "🍞"
            n.contains("galletita") -> "🍪"
            n.contains("lenteja") -> "🫘"
            n.contains("cacao") || n.contains("chocolate") -> "🍫"
            n.contains("cafe") || n.contains("café") -> "☕"
            n.contains("té") || n.contains("te") -> "🍵"
            n.contains("yerba") -> "🧉"
            n.contains("maiz") || n.contains("pisingallo") -> "🍿"
            n.contains("choclo") -> "🌽"
            n.contains("papa") || n.contains("batata") || n.contains("boniato") -> "🥔"
            n.contains("cebolla") -> "🧅"
            n.contains("naranja") -> "🍊"
            n.contains("morron") || n.contains("morrón") -> "🫑"
            n.contains("limon") || n.contains("limón") -> "🍋"
            n.contains("banana") -> "🍌"
            n.contains("manzana") -> "🍎"
            n.contains("zanahoria") -> "🥕"
            n.contains("lechuga") || n.contains("acelga") || n.contains("hoja") -> "🥬"
            n.contains("detergente") || n.contains("jabón liquido") || n.contains("jabón") -> "🧼"
            n.contains("lavandina") || n.contains("limpiador") || n.contains("cif") -> "✨"
            n.contains("papel higiénico") || n.contains("higiénico") || n.contains("rollo") -> "🧻"
            n.contains("shampoo") || n.contains("acondicionador") -> "🚿"
            n.contains("algodón") -> "☁️"
            n.contains("pañuelos") -> "🤧"
            n.contains("protectores diarios") || n.contains("tampones") || n.contains("toallitas") -> "♀️"
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