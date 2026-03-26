package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.gcendon.stockmaster.R

@Composable
fun StockMasterScaffold(
    showBackground: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (showBackground) {
            // Mismo fondo que el Login
            Image(
                painter = painterResource(id = R.drawable.bg_login),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Misma opacidad (0.4f) que nos gustó para que se vea la cocina
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)))
        }

        Scaffold(
            containerColor = Color.Transparent, // Clave para que se vea el fondo
            content = { padding -> content(padding) }
        )
    }
}