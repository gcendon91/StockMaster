package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gcendon.stockmaster.data.Product

@Composable
fun ProductCard(product: Product) {
    // Calculamos el "semáforo" inteligente
    val criticalThreshold = product.idealStock * 0.1 // 10%
    val lowThreshold = product.idealStock * 0.4      // 40%

    // Decidimos el color según los niveles
    val statusColor = when {
        product.currentStock <= criticalThreshold -> Color.Red // Crítico
        product.currentStock <= lowThreshold -> Color(0xFFFFC107) // Amarillo (Material 3 Amber)
        else -> Color(0xFF4CAF50) // Verde
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(160.dp), // Altura fija para que la grilla sea simétrica
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono con color dinámico
            Icon(
                imageVector = IconUtils.getProductIcon(product.name, product.category),
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                tint = statusColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del producto
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Stock y Unidad
            Text(
                text = "${product.currentStock} ${product.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = statusColor
            )

            // Indicador visual de categoría
            Surface(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape
            ) {
                Text(
                    text = product.category,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}