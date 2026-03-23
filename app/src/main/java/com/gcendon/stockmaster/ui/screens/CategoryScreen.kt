package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gcendon.stockmaster.data.Category
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    innerPadding: PaddingValues,
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()

    // Estados para los diálogos
    var showDialog by remember { mutableStateOf(false) }
    var categoryParaEditar by remember { mutableStateOf<Category?>(null) }
    var nombreEditado by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        // --- BOTÓN PARA CREAR ---
        floatingActionButton = {
            FloatingActionButton(onClick = {
                categoryParaEditar = null // Limpiamos por si acaso
                nombreEditado = ""
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Categoría")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            items(categories) { category ->
                ListItem(
                    headlineContent = { Text(category.name) },
                    trailingContent = {
                        Row {
                            // --- BOTÓN PARA EDITAR ---
                            IconButton(onClick = {
                                categoryParaEditar = category
                                nombreEditado = category.name
                                showDialog = true
                            }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            // --- BOTÓN PARA ELIMINAR ---
                            IconButton(onClick = { viewModel.deleteCategory(category.id) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Borrar",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }

        // --- DIÁLOGO ÚNICO (CREAR / EDITAR) ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (categoryParaEditar == null) "Nueva Categoría" else "Editar Categoría") },
                text = {
                    TextField(
                        value = nombreEditado,
                        onValueChange = { nombreEditado = it },
                        label = { Text("Nombre de la categoría") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (nombreEditado.isNotBlank()) {
                            if (categoryParaEditar == null) {
                                viewModel.addCategory(nombreEditado)
                            } else {
                                viewModel.updateCategory(categoryParaEditar!!, nombreEditado)
                            }
                            showDialog = false
                        }
                    }) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}