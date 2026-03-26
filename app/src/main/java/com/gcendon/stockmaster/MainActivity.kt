package com.gcendon.stockmaster

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.DisposableEffect
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
            FirebaseAuth.getInstance().currentUser
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

                //escucha cambios en la sesión.
                DisposableEffect(Unit) {
                    val listener = FirebaseAuth.AuthStateListener { auth ->
                        user = auth.currentUser
                    }
                    FirebaseAuth.getInstance().addAuthStateListener(listener)
                    onDispose { FirebaseAuth.getInstance().removeAuthStateListener(listener) }
                }
                LaunchedEffect(user) {
                    user?.let {
                        viewModel.setupUserAndHousehold(it.uid)
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
                                drawerContainerColor = Color(0xFFF8F9FF),
                                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                            ) {
                                val currentUser = FirebaseAuth.getInstance().currentUser

                                // 1. ENCABEZADO CON GRADIENTE
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF1A237E),
                                                    Color(0xFF3949AB)
                                                )
                                            )
                                        )
                                        .padding(
                                            top = 48.dp,
                                            start = 24.dp,
                                            end = 24.dp,
                                            bottom = 24.dp
                                        )
                                ) {
                                    Surface(
                                        modifier = Modifier.size(64.dp),
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.2f),
                                        border = BorderStroke(2.dp, Color.White)
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

                                Spacer(modifier = Modifier.height(24.dp))

                                // 2. SECCIÓN "TU HOGAR" (Corregida)
                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    Text(
                                        text = "TU HOGAR",
                                        style = typography.labelLarge,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                    )

                                    Surface(
                                        color = Color.White,
                                        tonalElevation = 2.dp,
                                        shadowElevation = 2.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically // Centra el ícono con el texto
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) { // Empuja el ícono a la derecha
                                                Text(
                                                    "Código de invitación",
                                                    style = typography.labelSmall,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = viewModel.inviteCode ?: "Generando...",
                                                    style = typography.titleMedium, // Un poco más grande
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1A237E)
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    viewModel.inviteCode?.let { code ->
                                                        clipboardManager.setText(
                                                            androidx.compose.ui.text.AnnotatedString(
                                                                code
                                                            )
                                                        )
                                                        android.widget.Toast.makeText(
                                                            context,
                                                            "Copiado: $code",
                                                            android.widget.Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                },
                                                modifier = Modifier
                                                    .background(
                                                        Color(0xFFE8EAF6),
                                                        CircleShape
                                                    )
                                                    .size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copiar",
                                                    tint = Color(0xFF1A237E),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Separador sutil
                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = Color.LightGray.copy(alpha = 0.4f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // 3. ITEMS DE NAVEGACIÓN
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
                                        icon = { Icon(icon, null) },
                                        label = {
                                            Text(
                                                label,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(route) {
                                                popUpTo("home") {
                                                    inclusive = false
                                                }
                                            }
                                            scope.launch { drawerState.close() }
                                        },
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = Color(0xFFE8EAF6),
                                            selectedTextColor = Color(0xFF1A237E),
                                            selectedIconColor = Color(0xFF1A237E),
                                            unselectedContainerColor = Color.Transparent,
                                            unselectedIconColor = Color.Gray
                                        ),
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 2.dp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f)) // Espacio flexible

                                // 4. ACCIONES DE PIE
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Add,
                                            null,
                                            tint = Color(0xFF4CAF50)
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Unirse a otro Hogar",
                                            color = Color(0xFF4CAF50)
                                        )
                                    },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        showJoinDialog = true
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.ExitToApp,
                                            null,
                                            tint = Color.Gray
                                        )
                                    },
                                    label = { Text("Cerrar Sesión", color = Color.Gray) },
                                    selected = false,
                                    onClick = { auth.signOut() },
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 12.dp
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
                                                context, mensaje, android.widget.Toast.LENGTH_SHORT
                                            ).show()

                                            if (success) {
                                                showJoinDialog =
                                                    false // Solo cerramos si el código era real
                                            }
                                        }
                                    })
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

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Unirse a un Hogar") }, text = {
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
    }, confirmButton = {
        Button(
            onClick = { if (code.isNotBlank()) onJoin(code.trim()) }, enabled = code.isNotBlank()
        ) {
            Text("Unirse")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) { Text("Cancelar") }
    })
}