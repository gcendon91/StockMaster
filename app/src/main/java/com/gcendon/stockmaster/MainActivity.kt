package com.gcendon.stockmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gcendon.stockmaster.ui.components.AddProductDialog
import com.gcendon.stockmaster.ui.screens.CategoryScreen
import com.gcendon.stockmaster.ui.screens.HomeScreen
import com.gcendon.stockmaster.ui.screens.ShoppingListScreen
import com.gcendon.stockmaster.ui.theme.StockMasterTheme
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ProductViewModel = viewModel()
            val navController = rememberNavController() // El GPS
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val categories by viewModel.categories.collectAsState()

            var showDialog by remember { mutableStateOf(false) }

            // Observamos en qué pantalla estamos para marcarla en el menú
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            StockMasterTheme {
                ModalNavigationDrawer(
                    drawerState = drawerState, drawerContent = {
                        ModalDrawerSheet(
                            // Le damos un fondo un poquito más oscuro para que contraste con la Home
                            drawerContainerColor = MaterialTheme.colorScheme.surface,
                            // Bordes redondeados profesionales solo a la derecha
                            drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                        ) {

                            // -----------------------------------------------------------------
                            // 1. EL ENCABEZADO (Header) CON COLOR Y PERSONALIDAD
                            // -----------------------------------------------------------------
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp) // Alto fijo para el encabezado
                                    // Usamos el color Primario de tu app (ese azul/violeta oscuro)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.BottomStart // Alineamos el texto abajo a la izquierda
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Un icono de Stock grande para darle identidad
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    // Título con mejor tipografía y color blanco
                                    Text(
                                        text = "Stock Master",
                                        style = typography.headlineMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Espaciador entre el header y los items
                            Spacer(modifier = Modifier.height(12.dp))

                            // -----------------------------------------------------------------
                            // 2. LOS ITEMS CON ICONOS Y MEJOR ESPACIADO
                            // -----------------------------------------------------------------

                            // Item 1: Inventario
                            NavigationDrawerItem(
                                // AGREGAMOS ICONO
                                icon = { Icon(Icons.Default.List, contentDescription = null) },
                                label = {
                                    Text(
                                        text = "Inventario de Productos",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                selected = currentRoute == "home",
                                onClick = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                    scope.launch { drawerState.close() }
                                },
                                // Les damos un padding horizontal para que no toquen el borde
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )

                            // Item 2: Gestionar Categorías
                            NavigationDrawerItem(
                                // AGREGAMOS ICONO (Usamos Settings para representar gestión)
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = {
                                    Text(
                                        text = "Gestionar Categorías",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                selected = currentRoute == "categories",
                                onClick = {
                                    navController.navigate("categories")
                                    scope.launch { drawerState.close() }
                                },
                                // Padding similar
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )

                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        Icons.Default.ShoppingCart, contentDescription = null
                                    )
                                }, label = {
                                    Text(
                                        "Lista de Compras", fontWeight = FontWeight.Medium
                                    )
                                }, selected = currentRoute == "shopping_list", onClick = {
                                    navController.navigate("shopping_list")
                                    scope.launch { drawerState.close() }
                                }, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(), floatingActionButton = {
                            // El botón de agregar producto solo aparece en la Home
                            if (currentRoute == "home") {
                                FloatingActionButton(onClick = { showDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                                }
                            }
                        }) { padding ->
                        NavHost(navController = navController, startDestination = "home") {
                            // RUTA 1: HOME
                            composable("home") {
                                HomeScreen(
                                    innerPadding = padding,
                                    viewModel = viewModel,
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onNavigateToShoppingList = { navController.navigate("shopping_list") })
                            }

                            // RUTA 2: CATEGORÍAS
                            composable("categories") {
                                CategoryScreen(
                                    innerPadding = padding,
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() })
                            }

                            // RUTA 3: LISTA DE COMPRAS
                            composable("shopping_list") {
                                ShoppingListScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() })
                            }
                        }

                        if (showDialog) {
                            AddProductDialog(
                                onDismiss = { showDialog = false },
                                categories = categories,
                                onConfirm = { n, c, s, u, i ->
                                    viewModel.addProduct(n, c, s, u, i)
                                    showDialog = false
                                },
                                onAddCategory = { viewModel.addCategory(it) })
                        }
                    }
                }
            }
        }
    }

}