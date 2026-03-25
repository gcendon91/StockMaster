package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcendon.stockmaster.ui.utils.IconUtils
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val itemsParaComprar by viewModel.shoppingList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Compras", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                // Usamos el mismo azul que en el Menú y la Home
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        if (itemsParaComprar.isEmpty()) {
            // UN ESTADO VACÍO MÁS LINDO
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.Gray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "¡Stock completo!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray
                )
                Text(
                    "No necesitás comprar nada por ahora.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)), // Un fondo apenas gris
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(itemsParaComprar) { prod ->
                    // Calculamos cuánto falta
                    prod.minStock - prod.currentStock

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    prod.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            supportingContent = {
                                Text(
                                    "Categoría: ${prod.category}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },

                            leadingContent = {
                                Text(
                                    text = IconUtils.getProductEmoji(prod.name, prod.category),
                                    fontSize = 32.sp
                                )
                            },

                            trailingContent = {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "Faltan: ${prod.minStock - prod.currentStock}",
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.headlineMedium
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = prod.unit,
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 2.dp) // Ajuste fino para la base
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}