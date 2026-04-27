package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcendon.stockmaster.R
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

    // Envolvemos todo en un Box para el fondo de imagen
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO DE IMAGEN (Igual que el resto de la app)
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Capa oscura para dar contraste
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)))

        Scaffold(
            containerColor = Color.Transparent, // Hacemos el Scaffold transparente
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "GESTIONAR CATEGORÍAS",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = Color(0xFF1A237E),
                    contentColor = Color.White,
                    onClick = {
                        categoryParaEditar = null
                        nombreEditado = ""
                        showDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Categoría")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp) // Espacio entre tarjetas
            ) {
                items(categories) { category ->
                    // Usamos una Card blanca para que resalte y no le afecte el modo oscuro
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF212121)
                        ),
                        elevation = cardElevation(defaultElevation = 4.dp)
                    ) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = {
                                Text(
                                    category.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = {
                                        categoryParaEditar = category
                                        nombreEditado = category.name
                                        showDialog = true
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = Color(0xFF3949AB)
                                        )
                                    }
                                    IconButton(onClick = { viewModel.deleteCategory(category.id) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Borrar",
                                            tint = Color(0xFFD32F2F)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // --- DIÁLOGO (Mantenemos tu lógica pero con estilo limpio) ---
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(if (categoryParaEditar == null) "Nueva Categoría" else "Editar Categoría") },
                    text = {
                        OutlinedTextField( // Outlined queda más pro
                            value = nombreEditado,
                            onValueChange = { nombreEditado = it },
                            label = { Text("Nombre") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (nombreEditado.isNotBlank()) {
                                    if (categoryParaEditar == null) {
                                        viewModel.addCategory(nombreEditado)
                                    } else {
                                        viewModel.updateCategory(
                                            categoryParaEditar!!.id,
                                            nombreEditado
                                        )
                                    }
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                        ) { Text("Guardar", color = Color.White) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }
}