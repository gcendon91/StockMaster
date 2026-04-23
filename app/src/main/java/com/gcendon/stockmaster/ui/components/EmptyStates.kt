package com.gcendon.stockmaster.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyInventoryState() {
    // 1. Rebote mucho más sutil (de 15f a 8f)
    val infiniteTransition = rememberInfiniteTransition()
    val dy by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f, // Movimiento corto y elegante
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing), // Más lento
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .padding(bottom = 120.dp), // Aumentamos la distancia al FAB
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = "¿Qué hay en casa?",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.6f),
                    offset = Offset(4f, 4f),
                    blurRadius = 12f
                )
            ),
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Cargá tus productos para controlar tu stock y generar listas de compras automáticamente.",
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.4f),
                    offset = Offset(2f, 2f),
                    blurRadius = 6f
                )
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp)) // Espacio entre texto y flecha

        // --- LA FLECHA "EDUCADA" ---
        Icon(
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = null,
            tint = Color(0xFF43A047),
            modifier = Modifier
                .size(40.dp)
                .offset(y = dy.dp) // Rebote cortito
        )
    }
}