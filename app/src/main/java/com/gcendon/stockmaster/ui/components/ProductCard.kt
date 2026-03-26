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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.utils.IconUtils

@Composable
fun ProductCard(
    item: Product,
    estaSeleccionado: Boolean,
    modoSeleccionActivo: Boolean
) {
    // Lógica de colores de stock (Mantenemos tu lógica impecable)
    val critico = item.minStock * 0.2
    val bajo = item.minStock * 0.5
    val colorEstado = when {
        item.currentStock <= critico -> Color(0xFFE53935)
        item.currentStock <= bajo -> Color(0xFFFFA000)
        else -> Color(0xFF43A047)
    }
    val stockFormateado = "%.1f".format(item.currentStock)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp), // <--- CAMBIO 1: Altura reducida (de 190 a 170)
        shape = RoundedCornerShape(20.dp), // Un poquito menos redondeado para estética compacta
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (estaSeleccionado) 10.dp else 4.dp // Elevación sutil
        ),
        border = if (estaSeleccionado) BorderStroke(
            2.dp,
            Color(0xFF1A237E)
        ) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado) Color(0xFFE8EAF6) else Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Indicador visual de stock (una barrita lateral sutil)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp) // <--- Achicamos la barra (de 6 a 4)
                    .background(colorEstado)
                    .align(Alignment.CenterStart)
            )

            if (modoSeleccionActivo) {
                Checkbox(
                    checked = estaSeleccionado,
                    onCheckedChange = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp) // <--- Padding de checkbox reducido
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // <--- CAMBIO 2: Paddings internos compactados
                    .padding(start = 12.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween // <--- Mejor distribución del espacio reducido
            ) {
                // Emoji Protagónico (Apenas más chico)
                Text(
                    text = IconUtils.getProductEmoji(item.name, item.category),
                    fontSize = 36.sp, // <--- CAMBIO 3: Emoji reducido (de 44 a 36)
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                // Nombre en negro sólido (Un punto más chico)
                Text(
                    text = item.name.uppercase(),
                    style = MaterialTheme.typography.titleSmall, // <--- CAMBIO 4: Nombre más compacto (de small a tiny)
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.5.sp // <--- Compactamos el espaciado
                )

                // Stock con tipografía pesada (Compactado)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = stockFormateado,
                        style = MaterialTheme.typography.titleLarge, // <--- CAMBIO 5: Número más compacto (de headline a titleLarge)
                        fontWeight = FontWeight.ExtraBold,
                        color = colorEstado,
                        lineHeight = 20.sp // Forzamos altura de línea compacta
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = item.unit,
                        style = MaterialTheme.typography.labelSmall, // <--- Unidad más chica
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 2.dp) // Alineación visual
                    )
                }

                // Categoría como un "Badge" más limpio y compacto
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(6.dp) // Bordes más finos
                ) {
                    Text(
                        text = item.category,
                        // <--- CAMBIO 6: Padding de badge reducido
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