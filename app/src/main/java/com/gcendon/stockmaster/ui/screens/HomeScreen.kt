package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import com.gcendon.stockmaster.ui.components.ProductCard
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(innerPadding: PaddingValues, viewModel: ProductViewModel) {
    val productList by viewModel.products.collectAsState()
    var seleccionados by remember { mutableStateOf(setOf<String>()) }
    val esModoSeleccion = seleccionados.isNotEmpty()

    Scaffold(
        topBar = {
            // Esta barra reemplaza a la anterior cuando seleccionamos algo
            TopAppBar(
                title = {
                    Text(if (esModoSeleccion) "${seleccionados.size} seleccionados" else "Mi Stock Hogareño")
                },
                navigationIcon = {
                    if (esModoSeleccion) {
                        IconButton(onClick = { seleccionados = emptySet() }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                actions = {
                    if (esModoSeleccion) {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (esModoSeleccion) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                    titleContentColor = if (esModoSeleccion) MaterialTheme.colorScheme.onPrimaryContainer else Color.White
                )
            )
        }
    ) { paddingInterno ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingInterno.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 8.dp,
                end = 8.dp
            )
        ) {
            items(productList, key = { it.id }) { producto ->
                val estaMarcado = seleccionados.contains(producto.id)

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .combinedClickable(
                            onClick = {
                                if (esModoSeleccion) {
                                    // Si estamos en modo selección, tildamos/destildamos
                                    seleccionados =
                                        if (estaMarcado) seleccionados - producto.id else seleccionados + producto.id
                                }
                                // Si NO estamos en modo selección, no hace NADA (evitamos falsos clics)
                            },
                            onLongClick = {
                                if (!esModoSeleccion) {
                                    seleccionados = setOf(producto.id)
                                }
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