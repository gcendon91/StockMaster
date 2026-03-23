package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String, Double) -> Unit
) {
    // Estos son los estados locales de lo que el usuario escribe
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Almacén") }
    var selectedUnit by remember { mutableStateOf("unid") }
    var stock by remember { mutableStateOf("") }

    val categories = listOf("Almacén", "Lácteos", "Carnicería", "Limpieza", "Verdulería", "Otros")
    val units = listOf("unid", "kg", "gr", "L", "ml", "pack", "frasco")

    var idealStock by remember { mutableStateOf("") }

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
                TextField(
                    value = idealStock,
                    onValueChange = { idealStock = it },
                    label = { Text("Stock Ideal ($selectedUnit)") },
                    placeholder = { Text("Ej: 5.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val stockDouble = stock.toDoubleOrNull() ?: 0.0
                val idealDouble = idealStock.toDoubleOrNull() ?: 1.0

                if (name.isNotBlank()) {
                    onConfirm(name, selectedCategory, stockDouble, selectedUnit, idealDouble)
                    onDismiss()
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}