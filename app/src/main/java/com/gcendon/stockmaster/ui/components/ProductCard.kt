package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.utils.IconUtils
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductCard(
    item: Product,
    estaSeleccionado: Boolean,
    modoSeleccionActivo: Boolean,
    productViewModel: ProductViewModel = viewModel()
) {
    // --- ESTADOS PARA EL DIÁLOGO RÁPIDO ---
    var showStockDialog by remember { mutableStateOf(false) }
    var tempStock by remember { mutableStateOf(TextFieldValue("")) }

    val focusRequester = remember { FocusRequester() }

    val colorEstado = when {
        item.currentStock <= item.minStock * 0.1 -> Color(0xFFE53935)
        item.currentStock < item.minStock -> Color(0xFFFFA000)
        else -> Color(0xFF43A047)
    }

    val stockFormateado = if (item.currentStock % 1.0 == 0.0)
        item.currentStock.toInt().toString()
    else
        "%.1f".format(item.currentStock)

    // --- DIÁLOGO DE EDICIÓN RÁPIDA ---
    if (showStockDialog) {
        AlertDialog(
            onDismissRequest = { showStockDialog = false },
            title = { Text("Ajustar Stock", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Ingresá la cantidad actual para ${item.name}:")
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempStock,
                        onValueChange = { tempStock = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester), // <--- Vinculamos el foco
                        suffix = { Text(item.unit) }
                    )

                    // EFECTO LANZADO: Apenas aparezca el diálogo, pedimos el foco
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Obtenemos el texto del objeto TextFieldValue
                    val textoLimpio = tempStock.text.replace(",", ".")
                    val nuevo = textoLimpio.toDoubleOrNull() ?: item.currentStock

                    productViewModel.updateStockDirectly(item, nuevo)
                    showStockDialog = false
                }) {
                    Text("GUARDAR", color = Color(0xFF43A047), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStockDialog = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (estaSeleccionado) 10.dp else 4.dp
        ),
        border = if (estaSeleccionado) BorderStroke(2.dp, Color(0xFF1A237E)) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado) Color(0xFFE8EAF6) else Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Indicador lateral
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(colorEstado)
                    .align(Alignment.CenterStart)
            )

            if (modoSeleccionActivo) {
                Checkbox(
                    checked = estaSeleccionado,
                    onCheckedChange = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. EMOJI
                Text(
                    text = IconUtils.getProductEmoji(
                        item.name,
                        item.category,
                        productViewModel.dynamicEmojiMap
                    ),
                    fontSize = 36.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                // 2. NOMBRE
                Text(
                    text = item.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium.copy(lineHeight = 16.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )

                // 3. SECCIÓN DE STOCK (DYNAMIC STEPPER + DIALOG)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    // BOTÓN MENOS (Click = -Step / LongClick = 0)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colorEstado.copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .combinedClickable(
                                    enabled = !modoSeleccionActivo && item.currentStock > 0,
                                    onClick = { productViewModel.quickUpdateStock(item, false) },
                                    onLongClick = { productViewModel.resetStock(item) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                null,
                                tint = colorEstado,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // NÚMERO DE STOCK (Click para Diálogo)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable(enabled = !modoSeleccionActivo) {
                                val valorActual = stockFormateado
                                // Cargamos el texto y seleccionamos todo para que sea fácil borrar
                                tempStock = TextFieldValue(
                                    text = valorActual,
                                    selection = TextRange(0, valorActual.length)
                                )
                                showStockDialog = true
                            }
                    ) {
                        Text(
                            text = stockFormateado,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = colorEstado
                        )
                        Text(
                            text = item.unit.lowercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }

                    // BOTÓN MÁS (Click = +Step)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colorEstado.copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(enabled = !modoSeleccionActivo) {
                                    productViewModel.quickUpdateStock(item, true)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                null,
                                tint = colorEstado,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // 4. CATEGORÍA (Badge)
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}