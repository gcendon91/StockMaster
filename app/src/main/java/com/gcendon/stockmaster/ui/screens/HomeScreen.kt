package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
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
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)))

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
                // --- BARRA DE BÚSQUEDA ESTILO LOGIN ---
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
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = Color.White
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
                                        if (!esModoSeleccion) seleccionados = setOf(producto.id)
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

            // Diálogos se mantienen igual
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