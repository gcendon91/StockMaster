package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String) -> Unit
) {
    // Estos son los estados locales de lo que el usuario escribe
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Almacén") }
    var selectedUnit by remember { mutableStateOf("unid") }
    var stock by remember { mutableStateOf("") }

    val categories = listOf("Almacén", "Lácteos", "Carnicería", "Limpieza", "Verdulería", "Otros")
    val units = listOf("unid", "kg", "gr", "L", "ml", "pack", "frasco")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de Categoría
                StockExposedDropdown(
                    label = "Categoría",
                    options = categories,
                    selectedOption = selectedCategory,
                    onOptionSelected = { selectedCategory = it }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    // Selector de Unidad
                    Box(modifier = Modifier.weight(1f)) {
                        StockExposedDropdown(
                            label = "Unidad",
                            options = units,
                            selectedOption = selectedUnit,
                            onOptionSelected = { selectedUnit = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val stockDouble = stock.toDoubleOrNull() ?: 0.0
                if (name.isNotBlank()) {
                    onConfirm(name, selectedCategory, stockDouble, selectedUnit)
                    onDismiss()
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}