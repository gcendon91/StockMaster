package com.gcendon.stockmaster

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.gcendon.stockmaster.ui.components.AddProductDialog
import com.gcendon.stockmaster.ui.components.HouseholdMembersSheet
import com.gcendon.stockmaster.ui.screens.CategoryScreen
import com.gcendon.stockmaster.ui.screens.HomeScreen
import com.gcendon.stockmaster.ui.screens.LoginScreen
import com.gcendon.stockmaster.ui.screens.OnboardingScreen
import com.gcendon.stockmaster.ui.screens.ShoppingListScreen
import com.gcendon.stockmaster.ui.theme.StockMasterTheme
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ProductViewModel = viewModel()
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val categories by viewModel.categories.collectAsState()
            val auth = FirebaseAuth.getInstance()

            // ESTADOS PRINCIPALES
            var showDialog by remember { mutableStateOf(false) }
            var showJoinDialog by remember { mutableStateOf(false) }
            var isUploadingPhoto by remember { mutableStateOf(false) }
            var userState by remember { mutableStateOf(auth.currentUser) }
            var showProfileOptions by remember { mutableStateOf(false) }
            val isFullyAuthenticated = userState != null && userState?.isEmailVerified == true
            val needsOnboarding = !viewModel.hasSeenOnboarding

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val context = LocalContext.current
            val clipboardManager = LocalClipboardManager.current

            var isLoadingCheck by remember { mutableStateOf(true) }

            var showMembersSheet by remember { mutableStateOf(false) }
            val members by viewModel.members.collectAsState()

            val db = FirebaseFirestore.getInstance()
            var isUpdateRequired by remember { mutableStateOf(false) }

            // Obtenemos la versión real instalada preguntándole al Sistema Operativo
            val currentVersion = remember {
                try {
                    val packageInfo =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            context.packageManager.getPackageInfo(
                                context.packageName,
                                android.content.pm.PackageManager.PackageInfoFlags.of(0)
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            context.packageManager.getPackageInfo(context.packageName, 0)
                        }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode.toInt()
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo.versionCode
                    }
                } catch (e: Exception) {
                    1 // Valor por defecto
                }
            }

            //Consultamos a Firestore si esta versión es válida
            LaunchedEffect(Unit) {
                db.collection("config").document("app_status")
                    .get()
                    .addOnSuccessListener { document ->
                        val minVersion = document.getLong("min_version") ?: 0L
                        isUpdateRequired = currentVersion < minVersion.toInt()
                        isLoadingCheck = false // YA TENEMOS LA RESPUESTA
                    }
                    .addOnFailureListener {
                        isLoadingCheck = false // SI FALLA, TAMBIÉN DEJAMOS DE CARGAR
                    }
            }

            val galleryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    scope.launch {
                        try {
                            isUploadingPhoto = true
                            val uid = userState?.uid ?: return@launch
                            val storageRef =
                                FirebaseStorage.getInstance().reference.child("profile_pictures/$uid/profile.jpg")

                            storageRef.putFile(uri).await()
                            val downloadUrl = storageRef.downloadUrl.await()

                            val profileUpdates = userProfileChangeRequest { photoUri = downloadUrl }
                            userState?.updateProfile(profileUpdates)?.await()
                            userState?.reload()?.await()

                            // Actualizamos el estado para que la UI se entere del cambio
                            userState = auth.currentUser

                            Toast.makeText(
                                context, "¡Foto actualizada!", Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                context, "Error al subir", Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isUploadingPhoto = false
                        }
                    }
                }
            }
            StockMasterTheme {
                // Escucha cambios en la sesión
                DisposableEffect(Unit) {
                    val listener = FirebaseAuth.AuthStateListener { currentAuth ->
                        userState = currentAuth.currentUser
                    }
                    auth.addAuthStateListener(listener)
                    onDispose { auth.removeAuthStateListener(listener) }
                }

                LaunchedEffect(userState) {
                    if (userState != null && userState?.isEmailVerified == true) {
                        viewModel.setupUserAndHousehold(userState!!.uid)
                        viewModel.listenToMembers()
                    }
                }

                if (isLoadingCheck) {
                    SplashScreenSimple()
                } else if (isUpdateRequired) {
                    UpdateRequiredScreen(context = context)
                } else if (!isFullyAuthenticated) {
                    LoginScreen(viewModel = viewModel)
                } else if (needsOnboarding) {
                    // Si está logueado pero no vio el tutorial, lo mandamos acá
                    OnboardingScreen(onFinished = {
                        viewModel.markOnboardingAsSeen(userState!!.uid)
                    })
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState, drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = Color(0xFFFDFDFF),
                                drawerShape = RoundedCornerShape(0.dp),
                                modifier = Modifier.width(320.dp)
                            ) {
                                // --- ENCABEZADO ---
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF1A237E), // Azul oscuro (Indigo 900)
                                                    Color(0xFF3949AB)  // Azul intermedio (Indigo 600)
                                                )
                                            )
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Surface(
                                                modifier = Modifier
                                                    .size(72.dp)
                                                    .clickable {
                                                        if (!isUploadingPhoto) showProfileOptions =
                                                            true
                                                    },
                                                shape = CircleShape,
                                                color = Color.White.copy(alpha = 0.2f),
                                                border = BorderStroke(2.dp, Color.White)
                                            ) {
                                                // USA userState DIRECTAMENTE AQUÍ
                                                if (userState?.photoUrl != null) {
                                                    AsyncImage(
                                                        model = userState?.photoUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Icon(
                                                        Icons.Default.AccountCircle,
                                                        null,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(4.dp),
                                                        tint = Color.White
                                                    )
                                                }
                                            }

                                            if (isUploadingPhoto) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(
                                                        72.dp
                                                    ), color = Color.White
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // USA userState PARA EL NOMBRE Y MAIL
                                        Text(
                                            text = userState?.displayName ?: "Usuario Master",
                                            style = typography.titleLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = userState?.email ?: "",
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
                                    color = Color.Gray,
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
                                                        AnnotatedString(
                                                            code
                                                        )
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "¡Código copiado!",
                                                        Toast.LENGTH_SHORT
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
                                                icon, null, modifier = Modifier.size(24.dp)
                                            )
                                        }, label = {
                                            Text(
                                                label.uppercase(),
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                                letterSpacing = 1.sp
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
                                            selectedIconColor = Color(0xFF1A237E),
                                            unselectedContainerColor = Color.Transparent,
                                            unselectedTextColor = Color.Gray,
                                            unselectedIconColor = Color.Gray
                                        ), modifier = Modifier.padding(
                                            horizontal = 16.dp, vertical = 4.dp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // 4. PIE DE PÁGINA: Acciones críticas
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        horizontal = 24.dp
                                    ), color = Color.LightGray.copy(alpha = 0.3f)
                                )

                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Add, null, tint = Color(0xFF43A047)
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
                                    icon = { Icon(Icons.Default.Group, null) },
                                    label = {
                                        Text(
                                            "MIEMBROS DEL HOGAR",
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        showMembersSheet = true
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )

                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.ExitToApp, null, tint = Color.LightGray
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
                                        start = 16.dp, end = 16.dp, bottom = 24.dp
                                    )
                                )
                            }
                        }) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            floatingActionButtonPosition = FabPosition.Center,
                            floatingActionButton = {
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
                                    onAddCategory = { viewModel.addCategory(it) })
                            }
                            if (showJoinDialog) {
                                JoinHouseholdDialog(
                                    onDismiss = { showJoinDialog = false },
                                    onJoin = { nuevoCodigo, alTerminar -> // Recibimos el callback 'alTerminar'
                                        viewModel.joinHousehold(nuevoCodigo) { success, mensaje ->
                                            // 1. Avisamos al diálogo que deje de cargar (sea éxito o error)
                                            alTerminar()

                                            // 2. Mostramos el mensaje (Toast)
                                            Toast.makeText(
                                                context, mensaje, Toast.LENGTH_SHORT
                                            ).show()

                                            // 3. Si fue exitoso, cerramos el diálogo
                                            if (success) {
                                                showJoinDialog = false
                                            }
                                        }
                                    })
                            }
                            if (showProfileOptions) {
                                AlertDialog(
                                    onDismissRequest = { showProfileOptions = false },
                                    icon = {
                                        Icon(
                                            Icons.Default.Face,
                                            contentDescription = null,
                                            tint = Color(0xFF1A237E)
                                        )
                                    },
                                    title = {
                                        Text(
                                            "Personalizar perfil",
                                            style = typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A237E)
                                        )
                                    },
                                    containerColor = Color(0xFFF5F6FA),

                                    text = {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp)
                                        ) {
                                            // Opción 1: Galería
                                            ProfileMenuOption(
                                                icon = Icons.Default.PhotoLibrary,
                                                text = "Elegir de la galería",
                                                color = Color(0xFF1A237E),
                                                onClick = {
                                                    showProfileOptions = false
                                                    galleryLauncher.launch("image/*")
                                                })

                                            // Opción 2: Foto de Google
                                            ProfileMenuOption(
                                                icon = Icons.Default.AccountCircle,
                                                text = "Restablecer foto de Google",
                                                color = Color(0xFF1A237E),
                                                onClick = {
                                                    showProfileOptions = false
                                                    val googleUrl =
                                                        userState?.providerData?.find { it.providerId == "google.com" }?.photoUrl
                                                    // LLAMAMOS A LA FUNCIÓN DE ACTUALIZACIÓN
                                                    actualizarYRefrescar(
                                                        googleUrl, scope, auth
                                                    ) { newUser -> userState = newUser }
                                                })

                                            HorizontalDivider(
                                                modifier = Modifier.padding(
                                                    vertical = 8.dp
                                                ), color = Color.LightGray.copy(alpha = 0.3f)
                                            )

                                            // Opción 3: Eliminar
                                            ProfileMenuOption(
                                                icon = Icons.Default.Delete,
                                                text = "Quitar foto actual",
                                                color = Color.Red,
                                                onClick = {
                                                    showProfileOptions = false
                                                    actualizarYRefrescar(
                                                        null, scope, auth
                                                    ) { newUser -> userState = newUser }
                                                })
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { showProfileOptions = false }) {
                                            Text(
                                                "Cerrar", color = Color(0xFF1A237E)
                                            )
                                        }
                                    })
                            }
                            if (showMembersSheet) {
                                HouseholdMembersSheet(
                                    onDismiss = { showMembersSheet = false },
                                    members = members,
                                    currentUserUid = userState?.uid,
                                    onRemoveMember = { uid ->
                                        viewModel.removeUserFromHousehold(uid)
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    private fun actualizarYRefrescar(
        nuevaUri: android.net.Uri?,
        scope: kotlinx.coroutines.CoroutineScope,
        auth: FirebaseAuth,
        onUserUpdated: (com.google.firebase.auth.FirebaseUser?) -> Unit
    ) {
        scope.launch {
            try {
                val user = auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    photoUri = nuevaUri
                }

                // 1. Subimos el cambio
                user?.updateProfile(profileUpdates)?.await()

                // 2. Traemos la info fresca del servidor
                user?.reload()?.await()

                // 3. EL TRUCO:
                // Ponemos el estado en null una fracción de segundo y luego el usuario nuevo.
                // Esto "despierta" a Compose y lo obliga a redibujar todo el Drawer.
                onUserUpdated(null)
                onUserUpdated(auth.currentUser)

            } catch (e: Exception) {
                // Error silencioso o un Toast
            }
        }
    }
}


@Composable
fun UpdateRequiredScreen(context: android.content.Context) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO COHERENTE (Igual a Login/Splash)
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. OVERLAY OSCURO MÁS DENSO (Para dar sensación de bloqueo/importancia)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // 3. CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono con estilo "Glass"
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, Color(0xFF43A047).copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF43A047), // Tu verde de stock
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "ACTUALIZACIÓN NECESARIA",
                color = Color.White,
                fontWeight = FontWeight.Black,
                style = typography.headlineSmall,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Para garantizar la seguridad de tus datos y la sincronización de tu hogar, es necesario instalar la última versión de StockMaster.",
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // BOTÓN DE ACCIÓN (Tu verde característico)
            Button(
                onClick = {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("market://details?id=com.gcendon.stockmaster")
                    )
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43A047),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "ACTUALIZAR AHORA",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Versión mínima requerida detectada",
                style = typography.labelSmall,
                color = Color.White.copy(alpha = 0.3f)
            )
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
        })
}

@Composable
fun ProfileMenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color? = null, // Usamos null por defecto
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        // Si color es null, usamos el azul indigo. Si no, usamos el color que venga (como el Rojo)
        Icon(
            imageVector = icon, contentDescription = null, tint = color ?: Color(0xFF3949AB)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = typography.bodyLarge,
            color = color ?: Color.Unspecified // Unspecified deja el color de texto por defecto
        )
    }
}

@Composable
fun SplashScreenSimple() {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Mantenemos el fondo para la consistencia visual
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Capa oscura para que el blanco resalte
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // 3. Contenido Central (Solo Ícono y Carga)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // El círculo con el ícono (Branding visual sin texto)
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // El indicador de carga
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}