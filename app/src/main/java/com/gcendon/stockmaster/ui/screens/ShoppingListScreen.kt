package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcendon.stockmaster.R
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.utils.IconUtils
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ProductViewModel, onBack: () -> Unit
) {
    val itemsParaComprar by viewModel.shoppingList.collectAsState()
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // ESTADOS PARA EL DIÁLOGO DE COMPRA RÁPIDA
    var showDialog by remember { mutableStateOf(false) }
    var productoSeleccionado by remember { mutableStateOf<Product?>(null) }
    var cantidadAComprar by remember { mutableStateOf("") }

    val categoriasDisponibles = remember(itemsParaComprar) {
        val nombresUnicos = itemsParaComprar.map { it.category }.distinct()

        val nombresOrdenados = nombresUnicos.sortedWith(
            compareBy<String> { it.equals("Otros", ignoreCase = true) }
                .thenBy { it }
        )

        listOf("Todas") + nombresOrdenados
    }

    val itemsFiltrados = remember(searchQuery, selectedCategory, itemsParaComprar) {
        itemsParaComprar.filter { prod ->
            val matchNombre = prod.name.contains(searchQuery, ignoreCase = true)
            val matchCategoria =
                if (selectedCategory == "Todas") true else prod.category == selectedCategory
            matchNombre && matchCategoria
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO DE PANTALLA
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Scaffold(
            containerColor = Color.Transparent, topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "LISTA DE COMPRAS",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    }, navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                    }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }) { innerPadding ->
            // CONTENEDOR VERTICAL (Buscador + Lista)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // --- BARRA DE BÚSQUEDA (FIJA) ---
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    placeholder = {
                        Text("Buscar producto...", color = Color.White.copy(alpha = 0.6f))
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = Color.White)
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Botón de Limpiar Búsqueda
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchQuery = ""; focusManager.clearFocus()
                                }) {
                                    Icon(Icons.Default.Clear, null, tint = Color.White)
                                }
                            }
                            // Botón de Filtro (Cambia a VERDE si hay filtro activo)
                            IconButton(onClick = { showSheet = true }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filtrar por categoría",
                                    tint = if (selectedCategory == "Todas") Color.White else Color(
                                        0xFF43A047
                                    )
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White
                    )
                )
                if (selectedCategory != "Todas") {
                    Text(
                        text = "Mostrando: $selectedCategory",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 28.dp, bottom = 8.dp)
                    )
                }
                // --- LISTA O ESTADO VACÍO ---
                if (itemsFiltrados.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isEmpty()) "¡TODO COMPLETO!" else "No hay coincidencias",
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp
                        ), verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemsFiltrados) { prod ->
                            val faltante = (prod.minStock - prod.currentStock).coerceAtLeast(0.0)
                            val colorUrgencia =
                                if (prod.currentStock <= prod.minStock * 0.1) Color(0xFFE53935) else Color(
                                    0xFFFFA000
                                )

                            val faltanteTxt = if (faltante % 1.0 == 0.0) faltante.toInt()
                                .toString() else "%.1f".format(faltante)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(3.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. IZQUIERDA: ICONO DINÁMICO (Firebase + Local)
                                    Text(
                                        text = IconUtils.getProductEmoji(
                                            prod.name,
                                            prod.category,
                                            viewModel.dynamicEmojiMap // <--- Conexión con la nube
                                        ), fontSize = 28.sp
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // 2. NOMBRE CON SOPORTE PARA 2 LÍNEAS
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prod.name.uppercase(),
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                lineHeight = 16.sp // Interlineado compacto
                                            ),
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF212121),
                                            maxLines = 2, // <--- Ahora soporta 2 renglones
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = prod.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // 3. DERECHA: INFO DE FALTANTE
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = "FALTAN",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorUrgencia,
                                            fontWeight = FontWeight.Black
                                        )
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = faltanteTxt,
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.Black,
                                                color = colorUrgencia,
                                                lineHeight = 26.sp
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = prod.unit,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = colorUrgencia,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // 4. BOTÓN DE COMPRA
                                    IconButton(
                                        onClick = {
                                            productoSeleccionado = prod
                                            cantidadAComprar =
                                                "%.1f".format(faltante).replace(",", ".")
                                            showDialog = true
                                        }, modifier = Modifier
                                            .size(38.dp)
                                            .background(
                                                Color(0xFF43A047), RoundedCornerShape(10.dp)
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.AddShoppingCart,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1A1A1A), // Un gris muy oscuro para el fondo
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        "Filtrar por Categoría",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Lista vertical de categorías
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categoriasDisponibles) { cat ->
                            val isSelected = selectedCategory == cat

                            Surface(
                                onClick = {
                                    selectedCategory = cat
                                    showSheet = false // Se cierra al elegir
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF43A047) else Color.White.copy(
                                    alpha = 0.1f
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (isSelected) Icons.Default.CheckCircle else Icons.Default.Label,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = cat,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // --- DIÁLOGO DE CONFIRMACIÓN DE COMPRA ---
        if (showDialog && productoSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmar Compra", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("¿Cuánto compraste de ${productoSeleccionado!!.name}?")
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = cantidadAComprar,
                            onValueChange = { cantidadAComprar = it },
                            label = { Text("Cantidad en ${productoSeleccionado!!.unit}") },
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
                            val cantidad = cantidadAComprar.toDoubleOrNull() ?: 0.0
                            if (cantidad > 0) {
                                viewModel.purchaseProduct(productoSeleccionado!!, cantidad)
                                showDialog = false
                            }
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E), contentColor = Color.White
                        )
                    ) {
                        Text("Sumar Stock")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                })
        }
    }
}