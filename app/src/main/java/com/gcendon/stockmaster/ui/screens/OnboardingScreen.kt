package com.gcendon.stockmaster.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcendon.stockmaster.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

val onboardingPages = listOf(
    OnboardingPage(
        "Tu alacena, siempre al día",
        "Sincronizá lo que tenés con lo que necesitás. Usá los accesos rápidos de + y - para un control total. Un toque largo en el - lleva el stock a cero.",
        Icons.Default.Kitchen
    ),
    OnboardingPage(
        "Hogar Compartido",
        "Desde el menú a la izquierda encontrarás tu Código de invitación para compartir con tu familia o la opción para Unirte a un hogar ya existente. Los cambios de stock se reflejan al instante en todos los dispositivos.",
        Icons.Default.Groups
    ),
    OnboardingPage(
        "Lista de Compras",
        "La app detecta automáticamente qué falta y lo carga a tu lista de compras. ¡Nunca más te olvides la leche!",
        Icons.Default.ShoppingCart
    ),
    OnboardingPage(
        "Reponer es Fácil",
        "Marcá lo que compraste y confirmá la cantidad. Stock Master repondrá tu inventario automáticamente para que siempre sepas qué tenés disponible.",
        Icons.Default.CheckCircle
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // FONDO (Consistencia con Login)
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Carrusel de páginas
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIdx ->
                val page = onboardingPages[pageIdx]
                OnboardingPageUI(page = page, pageIndex = pageIdx)
            }

            // Indicador de puntos (Dots)
            Row(
                Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.White else Color.White.copy(
                            alpha = 0.3f
                        )
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .background(color, CircleShape)
                    )
                }
            }

            // Botones de acción
            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 40.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (pagerState.currentPage == onboardingPages.size - 1) "¡ENTENDIDO!" else "SIGUIENTE",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OnboardingPageUI(page: OnboardingPage, pageIndex: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- AQUÍ ESTÁ EL CAMBIO: El "Mockup" de la funcionalidad ---
        Box(
            modifier = Modifier
                .fillMaxWidth() // <--- Ahora ocupa todo el ancho disponible
                .height(240.dp) // Mantenemos el alto fijo para que no se estire
                .padding(horizontal = 20.dp) // Un pequeño margen para que no toque los bordes
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (pageIndex) {
                0 -> StockMockup()    // Dibujamos un mini-stepper
                1 -> HogarMockup()    // Dibujamos un código de invitación
                2 -> ListaMockup()    // Dibujamos una lista de compras
                3 -> ReposicionMockup() // Dibujamos el diálogo de confirmación
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun StockMockup() {
    // Usamos el mismo estilo de tu Card
    Surface(
        modifier = Modifier.size(width = 160.dp, height = 180.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Tu indicador lateral (Color verde en este caso)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(Color(0xFF43A047))
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🥛", fontSize = 32.sp) // Tu Emoji
                Text(
                    "LECHE",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color(0xFF212121)
                )

                // TU STEPPER REAL
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(Color(0xFF43A047).copy(0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            null,
                            tint = Color(0xFF43A047),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            "2",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF43A047)
                        )
                        Text("u.", fontSize = 10.sp, color = Color.Gray)
                    }
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(Color(0xFF43A047).copy(0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            tint = Color(0xFF43A047),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Tu Badge de categoría
                Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        "LÁCTEOS",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun HogarMockup() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Simulación de la cabecera del Drawer
        Text(
            "GESTIÓN DE HOGAR",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // LA TARJETA QUE TIENES EN EL MAIN ACTIVITY
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF5F6FA), // El gris de tu código
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Código de invitación",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "AX-4502", // Código de ejemplo
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1A237E) // Tu azul Indigo
                    )
                }
                // El botoncito de copiar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color.LightGray.copy(0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        null,
                        tint = Color(0xFF1A237E),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // El botón de Unirse (el verde de abajo)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, null, tint = Color(0xFF43A047), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "UNIRSE A OTRO HOGAR",
                color = Color(0xFF43A047),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ListaMockup() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp), // Más margen lateral para que la tarjeta no sea tan ancha
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Emoji (Leche)
            Text(text = "🥛", fontSize = 28.sp)

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Nombre corto y Categoría
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LECHE", // Palabra corta = Cero saltos de línea
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121),
                    maxLines = 1 // Forzamos una sola línea por seguridad
                )
                Text(
                    text = "LÁCTEOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            // 3. Info de Faltante
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "FALTAN",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE53935), // Tu rojo de urgencia
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "2",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE53935)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 4. Botón verde de carrito
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFF43A047), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ReposicionMockup() {
    // Simulamos el AlertDialog de tu código
    Surface(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(24.dp), // Un poco más redondeado para el diálogo
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Confirmar Compra",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "¿Cuánto compraste?",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            // Simulación de tu OutlinedTextField
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(
                    "3",
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tu botón de Sumar Stock (Azul/Indigo)
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Sumar Stock", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}