package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
    val critico = item.minStock * 0.2
    val bajo = item.minStock * 0.5
    val colorEstado = when {
        item.currentStock <= critico -> Color.Red
        item.currentStock <= bajo -> Color(0xFFFFC107)
        else -> Color(0xFF4CAF50)
    }
    val stockFormateado = "%.1f".format(item.currentStock)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        // Si está seleccionado, le ponemos un borde azul
        border = if (estaSeleccionado) BorderStroke(
            3.dp,
            MaterialTheme.colorScheme.primary
        ) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Checkbox solo si estamos borrando
            if (modoSeleccionActivo) {
                Checkbox(
                    checked = estaSeleccionado,
                    onCheckedChange = null, // El clic lo maneja la HomeScreen
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = IconUtils.getProductEmoji(item.name, item.category), fontSize = 50.sp)
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$stockFormateado ${item.unit}",
                    fontWeight = FontWeight.Bold,
                    color = colorEstado
                )

                Surface(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    shape = CircleShape
                ) {
                    Text(
                        text = item.category,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}