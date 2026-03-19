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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcendon.stockmaster.data.Product
import com.gcendon.stockmaster.ui.utils.IconUtils

@Composable
fun ProductCard(product: Product) {
    // Lógica del semáforo (Rojo, Amarillo, Verde)
    val criticalThreshold = product.idealStock * 0.2
    val lowThreshold = product.idealStock * 0.5

    val statusColor = when {
        product.currentStock <= criticalThreshold -> Color.Red
        product.currentStock <= lowThreshold -> Color(0xFFFFC107) // Amarillo
        else -> Color(0xFF4CAF50) // Verde
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(180.dp), // Un poco más alta para que luzca el emoji
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // EL EMOJI GIGANTE
            Text(
                text = IconUtils.getProductEmoji(product.name, product.category),
                fontSize = 50.sp, // Bien grande y visual
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Stock con el color del semáforo
            Text(
                text = "${product.currentStock} ${product.unit}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            // Badge de categoría chiquito abajo
            Surface(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                shape = CircleShape
            ) {
                Text(
                    text = product.category,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}