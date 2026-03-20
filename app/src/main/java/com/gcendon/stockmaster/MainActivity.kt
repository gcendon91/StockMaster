package com.gcendon.stockmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gcendon.stockmaster.ui.components.AddProductDialog
import com.gcendon.stockmaster.ui.screens.HomeScreen
import com.gcendon.stockmaster.ui.theme.StockMasterTheme
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Instanciamos el "Cerebro" (ViewModel)
            val viewModel: ProductViewModel = viewModel()

            // 2. Creamos el "Interruptor" del Diálogo
            // 'remember' hace que el valor no se resetee al redibujar la pantalla
            var showDialog by remember { mutableStateOf(false) }

            StockMasterTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            // Al hacer click, encendemos el interruptor
                            onClick = { showDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                        }
                    }
                ) { padding ->
                    // Pasamos el viewModel a la HomeScreen para que lea de Firebase
                    HomeScreen(innerPadding = padding, viewModel = viewModel)

                    // Lógica del Diálogo: Si el interruptor está ON, mostramos el cartel
                    if (showDialog) {
                        AddProductDialog(
                            onDismiss = { showDialog = false },
                            onConfirm = { name, category, stock, unit ->
                                viewModel.addProduct(name, category, stock, unit)
                                showDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}


// Agregamos esto para ver los cambios instantáneamente en Android Studio
@Preview(showBackground = true)
@Composable
fun ProductPreview() {
    StockMasterTheme {
        //ProductCard(product = Product(name = "Aceite", currentStock = 2f, minStock = 1f))
    }
}