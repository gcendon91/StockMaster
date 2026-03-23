package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gcendon.stockmaster.data.Category

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    categories: List<Category>, // Esta es la lista que viene de Firebase
    onConfirm: (String, String, Double, String, Double) -> Unit,
    onAddCategory: (String) -> Unit // Función para guardar una categoría nueva
) {
    var name by remember { mutableStateOf("") }
    // Iniciamos con la primera categoría de la lista o "General"
    var selectedCategory by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("unid") }
    var stock by remember { mutableStateOf("") }
    var idealStock by remember { mutableStateOf("") }

    // Estado para el mini-diálogo de "Nueva Categoría"
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    // Si la lista de categorías cambia y no hay nada seleccionado, agarramos la primera
    LaunchedEffect(categories) {
        if (selectedCategory.isEmpty() && categories.isNotEmpty()) {
            selectedCategory = categories.first().name
        }
    }

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

                // Selector de Categoría + Botón de Agregar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StockExposedDropdown(
                            label = "Categoría",
                            options = categories.map { it.name }, // Convertimos objetos a texto
                            selectedOption = selectedCategory,
                            onOptionSelected = { selectedCategory = it }
                        )
                    }

                    // Botón para agregar categoría nueva
                    IconButton(
                        onClick = { showAddCategoryDialog = true },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Nueva Cat",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
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
                if (name.isNotBlank() && selectedCategory.isNotBlank()) {
                    onConfirm(name, selectedCategory, stockDouble, selectedUnit, idealDouble)
                    onDismiss()
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )

    // MINI-DIÁLOGO PARA AGREGAR CATEGORÍA
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Nueva Categoría") },
            text = {
                TextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nombre de la categoría") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        onAddCategory(newCategoryName)
                        selectedCategory = newCategoryName // La dejamos seleccionada
                        newCategoryName = ""
                        showAddCategoryDialog = false
                    }
                }) { Text("Agregar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancelar") }
            }
        )
    }
}