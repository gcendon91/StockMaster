package com.gcendon.stockmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.theme.StockMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockMasterTheme {
                // El Scaffold es el "esqueleto" de la pantalla (Barra arriba, botones abajo)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Column para apilar cosas verticalmente
                    Column(modifier = Modifier.padding(innerPadding)) {
                        ProductCard(
                            product = Product(
                                name = "Aceite de Oliva",
                                category = "Almacén",
                                currentStock = 0.5f,
                                minStock = 1f,
                                unit = "L"
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${product.category} • Stock: ${product.currentStock} ${product.unit}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Un indicador visual rápido
            val statusColor =
                if (product.currentStock <= product.minStock) Color.Red else Color.Green
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(statusColor, shape = CircleShape)
            )
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