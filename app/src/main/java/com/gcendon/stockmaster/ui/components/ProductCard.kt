package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.utils.IconUtils
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel

@Composable
fun ProductCard(
    item: Product,
    estaSeleccionado: Boolean,
    modoSeleccionActivo: Boolean,
    productViewModel: ProductViewModel = viewModel() // <--- Agregamos el ViewModel aquí
) {
    val colorEstado = when {
        item.currentStock <= item.minStock * 0.1 -> Color(0xFFE53935)
        item.currentStock < item.minStock -> Color(0xFFFFA000)
        else -> Color(0xFF43A047)
    }

    val stockFormateado = if (item.currentStock % 1.0 == 0.0)
        item.currentStock.toInt().toString()
    else
        "%.1f".format(item.currentStock)

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
            // Indicador lateral de stock
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
                // 1. EMOJI DINÁMICO (Viene de la nube o Local)
                Text(
                    text = IconUtils.getProductEmoji(
                        item.name,
                        item.category,
                        productViewModel.dynamicEmojiMap // <--- Usamos el mapa del VM
                    ),
                    fontSize = 36.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                // 2. NOMBRE (Soporte para 2 líneas y centrado)
                Text(
                    text = item.name.uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        lineHeight = 16.sp
                    ),
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center, // <--- Texto centrado
                    letterSpacing = 0.5.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )

                // 3. STOCK
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = stockFormateado,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorEstado,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = item.unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
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