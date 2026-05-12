package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcendon.stockmaster.R
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.components.AddProductDialog
import com.gcendon.stockmaster.ui.components.EmptyInventoryState
import com.gcendon.stockmaster.ui.components.ProductCard
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    viewModel: ProductViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToShoppingList: () -> Unit
) {
    val productList by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val focusManager = LocalFocusManager.current

    //estados
    var seleccionados by remember { mutableStateOf(setOf<String>()) }
    val esModoSeleccion = seleccionados.isNotEmpty()
    var productoAEditar by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    //filtros
    var selectedCategory by remember { mutableStateOf("Todas") }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val filteredProducts = productList?.filter { prod ->
        val matchSearch = prod.name.contains(searchQuery, ignoreCase = true) ||
                prod.category.contains(searchQuery, ignoreCase = true)
        val matchCategory =
            if (selectedCategory == "Todas") true else prod.category == selectedCategory
        matchSearch && matchCategory
    } ?: emptyList()

    val categoriasParaFiltrar = remember(categories) {
        val nombresOrdenados = categories.map { it.name }.sortedWith(
            compareBy<String> { it.equals("Otros", ignoreCase = true) }
                .thenBy { it }
        )
        listOf("Todas") + nombresOrdenados
    }

    // --- CAPA DE FONDO (Igual al Login) ---
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Capa oscura un poco más intensa (0.6f) para que resalten las tarjetas blancas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Scaffold(
            containerColor = Color.Transparent, // CLAVE: Scaffold transparente
            topBar = {
                CenterAlignedTopAppBar( // CenterAligned queda más elegante/premium
                    title = {
                        Text(
                            text = if (esModoSeleccion) "${seleccionados.size} seleccionados" else "MI STOCK",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        if (esModoSeleccion) {
                            IconButton(onClick = { seleccionados = emptySet() }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        } else {
                            IconButton(onClick = onOpenDrawer) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    actions = {
                        if (!esModoSeleccion) {
                            IconButton(onClick = onNavigateToShoppingList) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                viewModel.deleteMultipleProducts(seleccionados)
                                seleccionados = emptySet()
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFFF8A80)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = if (esModoSeleccion) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { paddingInterno ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingInterno.calculateTopPadding())
            ) {
                when {
                    productList == null -> {
                        // ESTADO 1: CARGANDO (Firestore todavía no respondió)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    productList!!.isEmpty() -> {
                        // ESTADO 2: LA ALACENA ESTÁ REALMENTE VACÍA
                        EmptyInventoryState()
                    }

                    else -> {
                        // ESTADO 3: HAY PRODUCTOS

                        // 1. Buscador
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            placeholder = {
                                Text(
                                    "¿Qué estás buscando?",
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = {
                                            searchQuery = ""; focusManager.clearFocus()
                                        }) {
                                            Icon(Icons.Default.Clear, null, tint = Color.White)
                                        }
                                    }
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
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.15f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        // Mensaje si hay un filtro activo
                        if (selectedCategory != "Todas") {
                            Text(
                                text = "Mostrando: $selectedCategory",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                            )
                        }

                        // --- GRILLA DE PRODUCTOS ---
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                bottom = paddingInterno.calculateBottomPadding() + 80.dp,
                                start = 12.dp,
                                end = 12.dp,
                                top = 8.dp
                            )
                        ) {
                            items(filteredProducts, key = { it.id }) { producto ->
                                val estaMarcado = seleccionados.contains(producto.id)
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .combinedClickable(
                                            onClick = {
                                                if (esModoSeleccion) {
                                                    seleccionados =
                                                        if (estaMarcado) seleccionados - producto.id else seleccionados + producto.id
                                                } else {
                                                    productoAEditar = producto
                                                }
                                            },
                                            onLongClick = {
                                                if (!esModoSeleccion) seleccionados =
                                                    setOf(producto.id)
                                            }
                                        )
                                ) {
                                    ProductCard(
                                        item = producto,
                                        estaSeleccionado = estaMarcado,
                                        modoSeleccionActivo = esModoSeleccion
                                    )
                                }
                            }
                        }
                    }
                }
                if (showSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showSheet = false },
                        sheetState = sheetState,
                        containerColor = Color(0xFF1C1C1C),
                        contentColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
                        ) {
                            Text(
                                "Filtrar por Categoría",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(categoriasParaFiltrar) { cat ->
                                    val isSelected = selectedCategory == cat
                                    Surface(
                                        onClick = {
                                            selectedCategory = cat
                                            showSheet = false
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) Color(0xFF43A047) else Color.White.copy(
                                            alpha = 0.05f
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
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = cat,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (productoAEditar != null) {
                    AddProductDialog(
                        product = productoAEditar,
                        onDismiss = { productoAEditar = null },
                        categories = categories,
                        onConfirm = { nombre, categoria, stock, unidad, ideal ->
                            viewModel.updateProduct(
                                productoAEditar!!.id,
                                nombre,
                                categoria,
                                stock,
                                unidad,
                                ideal
                            )
                            productoAEditar = null
                        },
                        onAddCategory = { viewModel.addCategory(it) }
                    )
                }
            }
        }
    }
}