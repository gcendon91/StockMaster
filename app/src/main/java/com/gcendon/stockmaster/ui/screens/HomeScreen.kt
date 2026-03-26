package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.components.AddProductDialog
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
    var seleccionados by remember { mutableStateOf(setOf<String>()) }
    val esModoSeleccion = seleccionados.isNotEmpty()
    var productoAEditar by remember { mutableStateOf<Product?>(null) }

    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = productList.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(
            searchQuery, ignoreCase = true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (esModoSeleccion) "${seleccionados.size} seleccionados" else "Mi Stock Hogareño")
                }, navigationIcon = {
                    // --- ESTA ES LA LÓGICA DE INTERRUPTOR ---
                    if (esModoSeleccion) {
                        // Si estamos seleccionando, mostramos la X
                        IconButton(onClick = { seleccionados = emptySet() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar selección")
                        }
                    } else {
                        // SI NO, mostramos el Menú para abrir el Drawer
                        IconButton(onClick = onOpenDrawer) { // <--- ACÁ LLAMAMOS AL DRAWER
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú lateral",
                                tint = Color.White // Para que combine con tu título blanco
                            )
                        }
                    }
                }, actions = {
                    if (!esModoSeleccion) {
                        IconButton(onClick = onNavigateToShoppingList) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    } else {
                        // Acá ya tenías tu botón de borrar (el tacho de basura)
                        IconButton(onClick = {
                            viewModel.deleteMultipleProducts(seleccionados)
                            seleccionados = emptySet()
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Borrar",
                                tint = Color.Red
                            )
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (esModoSeleccion) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                    titleContentColor = if (esModoSeleccion) MaterialTheme.colorScheme.onPrimaryContainer else Color.White,
                    // Agregamos esto para que el icono del menú también sea blanco
                    navigationIconContentColor = if (esModoSeleccion) MaterialTheme.colorScheme.onPrimaryContainer else Color.White
                )
            )
        }) { paddingInterno ->
        // 1. Envolvemos todo en una Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingInterno.calculateTopPadding()) // Respetamos la TopBar
        ) {
            // 2. LA BARRA DE BÚSQUEDA
            androidx.compose.material3.OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar producto o categoría...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Limpiar"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // 3. LA GRILLA (Ahora usa filteredProducts en lugar de productList)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = paddingInterno.calculateBottomPadding() + 80.dp,
                    start = 8.dp,
                    end = 8.dp
                )
            ) {
                // USAMOS LA LISTA FILTRADA ACÁ:
                items(filteredProducts, key = { it.id }) { producto ->
                    val estaMarcado = seleccionados.contains(producto.id)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .combinedClickable(onClick = {
                                if (esModoSeleccion) {
                                    // Si estamos en modo selección, tildamos/destildamos
                                    seleccionados =
                                        if (estaMarcado) seleccionados - producto.id else seleccionados + producto.id
                                } else {
                                    productoAEditar = producto
                                }
                            }, onLongClick = {
                                if (!esModoSeleccion) {
                                    seleccionados = setOf(producto.id)
                                }
                            })
                    ) {
                        ProductCard(
                            item = producto,
                            estaSeleccionado = estaMarcado,
                            modoSeleccionActivo = esModoSeleccion
                        )
                    }
                }
            }
            if (productoAEditar != null) {
                AddProductDialog(
                    product = productoAEditar, // <--- Le pasamos el producto
                    onDismiss = { productoAEditar = null },
                    categories = categories,
                    onConfirm = { nombre, categoria, stock, unidad, ideal ->
                        viewModel.updateProduct(
                            productoAEditar!!.id, nombre, categoria, stock, unidad, ideal
                        )
                        productoAEditar = null
                    },
                    onAddCategory = { viewModel.addCategory(it) })
            }
        }
    }
}