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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val itemsParaComprar by viewModel.shoppingList.collectAsState()

    // ESTADOS PARA EL DIÁLOGO DE COMPRA RÁPIDA
    var showDialog by remember { mutableStateOf(false) }
    var productoSeleccionado by remember { mutableStateOf<Product?>(null) }
    var cantidadAComprar by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO DE PANTALLA
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)))

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "LISTA DE COMPRAS",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            // 2. CONTENEDOR PRINCIPAL (Usa el padding del Scaffold)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (itemsParaComprar.isEmpty()) {
                    // ESTADO: TODO COMPRADO
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = Color(0xFF43A047).copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "¡ALACENA LLENA!",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                } else {
                    // LISTA DE PRODUCTOS FALTANTES
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(itemsParaComprar) { prod ->
                            // Cálculos previos para evitar errores de literales
                            val faltante = prod.minStock - prod.currentStock
                            val faltanteTexto = "FALTAN: %.1f".format(faltante)
                            val colorUrgencia = if (prod.currentStock <= prod.minStock * 0.1)
                                Color(0xFFE53935) else Color(0xFFFFA000)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Icono Emoji
                                    Text(
                                        text = IconUtils.getProductEmoji(prod.name, prod.category),
                                        fontSize = 32.sp
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Información
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prod.name.uppercase(),
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF212121),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Tengo: ${prod.currentStock} / Mín: ${prod.minStock}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }

                                    // Acción y Urgencia
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = faltanteTexto,
                                            color = colorUrgencia,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        IconButton(
                                            onClick = {
                                                productoSeleccionado = prod
                                                // Sugerimos el faltante por defecto
                                                cantidadAComprar =
                                                    "%.1f".format(faltante).replace(",", ".")
                                                showDialog = true
                                            },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    Color(0xFF43A047),
                                                    RoundedCornerShape(10.dp)
                                                )
                                        ) {
                                            Icon(
                                                Icons.Default.AddShoppingCart,
                                                null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
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
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                    ) {
                        Text("Sumar Stock")
                    }
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