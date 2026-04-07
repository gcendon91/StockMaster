package com.gcendon.stockmaster

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
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

            val user = FirebaseAuth.getInstance().currentUser
            val photoUrl = user?.photoUrl // URL mágica de Google
            val userName = user?.displayName ?: "Usuario"
            val userEmail = user?.email ?: ""

            val galleryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    // Por ahora lo dejamos así, luego podrías subirlo a Firebase Storage
                    // o guardarlo en una variable de estado para que se vea el cambio.
                }
            }

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
                                drawerContainerColor = Color(0xFFFDFDFF), // Un blanco casi puro pero con un toque frío
                                drawerShape = RoundedCornerShape(
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                ), // Recto en los bordes queda más moderno si ocupa todo el lateral
                                modifier = Modifier.width(320.dp) // Un ancho estándar pro
                            ) {
                                FirebaseAuth.getInstance().currentUser

                                // 1. ENCABEZADO: Ahora con el fondo de la cocina pero muy desenfocado (o el gradiente pro)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF1A237E),
                                                    Color(0xFF3949AB)
                                                )
                                            )
                                        )
                                ) {
                                    // Podrías poner una imagen de fondo aquí también con opacidad baja
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clickable { galleryLauncher.launch("image/*") }, // <--- Al tocar, elige foto
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.2f),
                                            border = BorderStroke(2.dp, Color.White)
                                        ) {
                                            // Si currentUser tiene foto de Google (photoUrl), AsyncImage la carga sola.
                                            // Si no, muestra el icono por defecto.
                                            if (photoUrl != null) {
                                                AsyncImage(
                                                    model = photoUrl, // <--- Usamos la variable limpia
                                                    contentDescription = "Foto de perfil",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Icon(
                                                    Icons.Default.AccountCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(4.dp),
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = userName,
                                            style = typography.titleLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = userEmail,
                                            style = typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // 2. SECCIÓN "TU HOGAR": Estilo Tarjeta Glass
                                Text(
                                    text = "GESTIÓN DE HOGAR",
                                    style = typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )

                                Surface(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth(),
                                    color = Color(0xFFF5F6FA),
                                    shape = RoundedCornerShape(20.dp)
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
                                                text = viewModel.inviteCode ?: "Generando...",
                                                style = typography.headlineSmall,
                                                fontWeight = FontWeight.Black,
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
                                                        "¡Código copiado!",
                                                        android.widget.Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            modifier = Modifier
                                                .background(Color.White, CircleShape)
                                                .size(40.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                null,
                                                tint = Color(0xFF1A237E),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // 3. MENÚ DE NAVEGACIÓN: Con más aire y mejores íconos
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
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                label.uppercase(),
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                                letterSpacing = 1.sp
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
                                            unselectedTextColor = Color.Gray,
                                            unselectedIconColor = Color.Gray
                                        ),
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 4.dp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // 4. PIE DE PÁGINA: Acciones críticas
                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier.padding(
                                        horizontal = 24.dp
                                    ), color = Color.LightGray.copy(alpha = 0.3f)
                                )

                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Add,
                                            null,
                                            tint = Color(0xFF43A047)
                                        )
                                    },
                                    label = {
                                        Text(
                                            "UNIRSE A OTRO HOGAR",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF43A047)
                                        )
                                    },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        showJoinDialog = true
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.ExitToApp,
                                            null,
                                            tint = Color.LightGray
                                        )
                                    },
                                    label = {
                                        Text(
                                            "CERRAR SESIÓN",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.LightGray
                                        )
                                    },
                                    selected = false,
                                    onClick = { auth.signOut() },
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 24.dp
                                    )
                                )
                            }
                        }) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(), floatingActionButton = {
                                // El botón de agregar producto solo aparece en la Home
                                if (currentRoute == "home") {
                                    FloatingActionButton(
                                        onClick = { showDialog = true },
                                        containerColor = Color(0xFF1A237E),
                                        contentColor = Color.White
                                    ) {
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
                                    },
                                    onAddCategory = { viewModel.addCategory(it) }
                                )
                            }
                            if (showJoinDialog) {
                                JoinHouseholdDialog(
                                    onDismiss = { showJoinDialog = false },
                                    onJoin = { nuevoCodigo, alTerminar -> // Recibimos el callback 'alTerminar'
                                        viewModel.joinHousehold(nuevoCodigo) { success, mensaje ->
                                            // 1. Avisamos al diálogo que deje de cargar (sea éxito o error)
                                            alTerminar()

                                            // 2. Mostramos el mensaje (Toast)
                                            android.widget.Toast.makeText(
                                                context,
                                                mensaje,
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()

                                            // 3. Si fue exitoso, cerramos el diálogo
                                            if (success) {
                                                showJoinDialog = false
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
fun JoinHouseholdDialog(
    onDismiss: () -> Unit,
    onJoin: (String, onResult: () -> Unit) -> Unit // Agregamos un callback de finalización
) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Unirse a un Hogar") },
        text = {
            Column {
                Text("Ingresá el código de 6 caracteres:")
                OutlinedTextField(
                    value = code,
                    onValueChange = { if (it.length <= 6) code = it.uppercase() },
                    placeholder = { Text("Ej: A8J3K2") },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading // Deshabilitar mientras carga
                )
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    // Llamamos a unJoin y le pasamos qué hacer cuando termine
                    onJoin(code) {
                        isLoading = false // <--- ESTO es lo que lo destraba si falla
                    }
                },
                enabled = code.length == 6 && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
            ) {
                Text(if (isLoading) "Verificando..." else "Unirse")
            }
        },
        dismissButton = {
            // El botón cancelar ahora solo se oculta si está cargando,
            // pero si falla, vuelve a aparecer
            if (!isLoading) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        }
    )
}