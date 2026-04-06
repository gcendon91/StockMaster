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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gcendon.stockmaster.data.Category
import com.gcendon.stockmaster.data.Product
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    product: Product? = null,
    onDismiss: () -> Unit,
    categories: List<Category>,
    onConfirm: (String, String, Double, String, Double) -> Unit,
    onAddCategory: (String) -> Unit
) {
    // --- ESTADOS ---
    var name by remember { mutableStateOf(product?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(product?.category ?: "") }
    var selectedUnit by remember { mutableStateOf(product?.unit ?: "unid") }
    var stock by remember { mutableStateOf(product?.currentStock?.toString() ?: "") }
    var idealStock by remember { mutableStateOf(product?.minStock?.toString() ?: "") }

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val units = listOf("unid", "kg", "gr", "L", "ml", "pack", "frasco")

    // FIX DEL FOCO: Usamos un delay de 100ms para asegurar que el diálogo ya cargó
    LaunchedEffect(Unit) {
        delay(400)
        focusRequester.requestFocus()
    }

    // Sincronizar categoría inicial si está vacío
    LaunchedEffect(categories) {
        if (selectedCategory.isEmpty() && categories.isNotEmpty()) {
            selectedCategory = categories.first().name
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Nuevo Producto" else "Editar Producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester), // ASIGNACIÓN DEL FOCO
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StockExposedDropdown(
                            label = "Categoría",
                            options = categories.map { it.name },
                            selectedOption = selectedCategory,
                            onOptionSelected = { selectedCategory = it }
                        )
                    }

                    IconButton(
                        onClick = { showAddCategoryDialog = true },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Nueva Cat",
                            tint = MaterialTheme.colorScheme.primary // COLOR ORIGINAL
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val stockDouble = stock.toDoubleOrNull() ?: 0.0
                    val idealDouble = idealStock.toDoubleOrNull() ?: 1.0

                    if (name.isNotBlank() && selectedCategory.isNotBlank()) {
                        // 1. Guardamos el producto
                        onConfirm(name, selectedCategory, stockDouble, selectedUnit, idealDouble)

                        // 2. Decidimos si cerrar o limpiar
                        if (product == null) {
                            // Es NUEVO: No cerramos, solo limpiamos para el siguiente
                            name = ""
                            stock = ""
                            idealStock = ""
                            selectedUnit = "unid"
                            // Re-pedimos el foco para el siguiente producto
                            focusRequester.requestFocus()
                        } else {
                            // Es EDICIÓN: Cerramos el diálogo normalmente
                            onDismiss()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A237E),
                    contentColor = Color.White
                )
            ) {
                Text(if (product == null) "Guardar y Seguir" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar") // COLOR/TEXTO ORIGINAL
            }
        }
    )
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Nueva Categoría") },
            text = {
                TextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        onAddCategory(newCategoryName)
                        selectedCategory = newCategoryName
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