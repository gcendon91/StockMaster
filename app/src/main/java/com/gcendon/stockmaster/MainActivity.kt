package com.gcendon.stockmaster

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.gcendon.stockmaster.ui.screens.LoginScreen
import com.gcendon.stockmaster.ui.screens.ShoppingListScreen
import com.gcendon.stockmaster.ui.theme.StockMasterTheme
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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

            val auth = FirebaseAuth.getInstance()

            var showDialog by remember { mutableStateOf(false) }

            // Observamos en qué pantalla estamos para marcarla en el menú
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val context = androidx.compose.ui.platform.LocalContext.current
            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

            StockMasterTheme {
                // 1. Creamos un estado que recuerde al usuario actual
                var user by remember { mutableStateOf(auth.currentUser) }

                // 2. Escuchamos cambios (cuando se loguea o sale)
                LaunchedEffect(Unit) {
                    auth.addAuthStateListener { firebaseAuth ->
                        user = firebaseAuth.currentUser
                    }
                }
                if (user == null) {
                    // Si no hay nadie, mostramos la pantalla de login que hiciste
                    LoginScreen(viewModel = viewModel)
                } else {
                    val auth = FirebaseAuth.getInstance()
                    auth.currentUser
                    var showJoinDialog by remember { mutableStateOf(false) }

                    ModalNavigationDrawer(
                        drawerState = drawerState, drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = Color(0xFFF8F9FF), // Un blanco azulado muy suave
                                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                            ) {
                                val currentUser = FirebaseAuth.getInstance().currentUser

                                // --- ENCABEZADO MODERNO ---
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF1A237E), Color(0xFF3949AB)
                                                )
                                            )
                                        )
                                        .padding(
                                            top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp
                                        )
                                ) {
                                    // Círculo de Perfil
                                    Surface(
                                        modifier = Modifier.size(64.dp),
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            2.dp, Color.White
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(4.dp),
                                            tint = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = currentUser?.displayName ?: "Usuario",
                                        style = typography.headlineSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = currentUser?.email ?: "",
                                        style = typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // --- TARJETA DE CÓDIGO (Más pro) ---
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp, vertical = 8.dp
                                    )
                                ) {
                                    Text(
                                        "TU HOGAR",
                                        style = typography.labelLarge,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                    )

                                    Surface(
                                        color = Color.White,
                                        tonalElevation = 4.dp,
                                        shadowElevation = 2.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    "Código de invitación",
                                                    style = typography.labelSmall,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = viewModel.householdId ?: "Cargando...",
                                                    style = typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1A237E)
                                                )
                                            }
                                            IconButton(onClick = {
                                                viewModel.householdId?.let { id ->
                                                    // ESTO COPIA EL TEXTO AL PORTAPAPELES
                                                    clipboardManager.setText(
                                                        androidx.compose.ui.text.AnnotatedString(
                                                            id
                                                        )
                                                    )

                                                    // OPCIONAL: Un cartelito (Toast) para avisar que se copió
                                                    android.widget.Toast.makeText(
                                                        context,
                                                        "Código copiado al portapapeles",
                                                        android.widget.Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copiar",
                                                    tint = Color(0xFF1A237E),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier.padding(
                                        horizontal = 24.dp
                                    ), color = Color.LightGray.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // --- ITEMS DE NAVEGACIÓN ---
                                val items = listOf(
                                    Triple("Inventario", Icons.Default.List, "home"),
                                    Triple("Categorías", Icons.Default.Settings, "categories"),
                                    Triple(
                                        "Lista de Compras",
                                        Icons.Default.ShoppingCart,
                                        "shopping_list"
                                    )
                                )

                                items.forEach { (label, icon, route) ->
                                    val isSelected = currentRoute == route
                                    NavigationDrawerItem(
                                        icon = {
                                            Icon(
                                                icon,
                                                null,
                                                tint = if (isSelected) Color(0xFF1A237E) else Color.Gray
                                            )
                                        }, label = {
                                            Text(
                                                label,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }, selected = isSelected, onClick = {
                                            navController.navigate(route) {
                                                popUpTo("home") {
                                                    inclusive = false
                                                }
                                            }
                                            scope.launch { drawerState.close() }
                                        }, colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = Color(0xFFE8EAF6),
                                            selectedTextColor = Color(0xFF1A237E),
                                            unselectedContainerColor = Color.Transparent
                                        ), modifier = Modifier.padding(
                                            horizontal = 12.dp, vertical = 2.dp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f)) // Empuja lo que sigue hacia abajo

                                // --- ACCIONES DE CUENTA ---
                                NavigationDrawerItem(
                                    icon = {
                                    Icon(
                                        Icons.Default.Add, null, tint = Color(0xFF4CAF50)
                                    )
                                    },
                                    label = {
                                        Text(
                                            "Unirse a otro Hogar", color = Color(0xFF4CAF50)
                                        )
                                    },
                                    selected = false,
                                    onClick = { showJoinDialog = true },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.ExitToApp, null, tint = Color.Gray
                                        )
                                    },
                                    label = { Text("Cerrar Sesión", color = Color.Gray) },
                                    selected = false,
                                    onClick = { auth.signOut() },
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp, vertical = 12.dp
                                    )
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
                            if (showJoinDialog) {
                                JoinHouseholdDialog(
                                    onDismiss = { showJoinDialog = false },
                                    onJoin = { nuevoCodigo ->
                                        // Usamos la versión con validación que te pasé antes
                                        viewModel.joinHousehold(nuevoCodigo) { success, mensaje ->
                                            // Mostramos el Toast (el cartelito negro) con el resultado
                                            android.widget.Toast.makeText(
                                                context,
                                                mensaje,
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()

                                            if (success) {
                                                showJoinDialog =
                                                    false // Solo cerramos si el código era real
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun JoinHouseholdDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unirse a un Hogar") },
        text = {
            Column {
                Text("Pegá el código del hogar:")
                TextField(
                    value = code,
                    onValueChange = { code = it },
                    placeholder = { Text("Ej: abc123def...") },
                    modifier = Modifier.padding(top = 8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (code.isNotBlank()) onJoin(code.trim()) },
                enabled = code.isNotBlank()
            ) {
                Text("Unirse")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}