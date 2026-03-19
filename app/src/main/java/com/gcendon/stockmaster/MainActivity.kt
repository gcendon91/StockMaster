package com.gcendon.stockmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.theme.StockMasterTheme
import com.gcendon.stockmaster.ui.screens.HomeScreen
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import com.gcendon.stockmaster.ui.components.ProductCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class) // Material 3 usa algunas APIs experimentales aún
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockMasterTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Mi Stock Hogareño") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    },
                    floatingActionButton = {
                        // El FAB es el botón circular característico de Android
                        FloatingActionButton(
                            onClick = {
                                /* Por ahora no hace nada, aquí abriremos un formulario */
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                        }
                    }
                ) { padding ->
                    HomeScreen(innerPadding = padding)
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
        ProductCard(product = Product(name = "Aceite", currentStock = 2f, minStock = 1f))
    }
}