package com.gcendon.stockmaster.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcendon.stockmaster.R
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: ProductViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val credentialManager = androidx.credentials.CredentialManager.create(context)
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var emailToReset by remember { mutableStateOf("") }
    var showVerificationSent by remember { mutableStateOf(false) }

    fun handleGoogleSignIn() {
        val googleIdOption =
            com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("776569543507-kackuq8mm4lijob2kj9c3k20lhc476hj.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build()

        val request = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context = context, request = request)
                val googleIdTokenCredential =
                    com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(
                        result.credential.data
                    )
                val idToken = googleIdTokenCredential.idToken

                viewModel.signInWithGoogle(idToken) { error ->
                    if (error != null) errorMessage = error
                }
            } catch (e: Exception) {
                errorMessage = "Error al conectar con Google"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. FONDO (Luminoso como te gustó)
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa oscura sutil (0.4f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- MARCA PROTAGONISTA ---
            Text(
                text = "Stock\nMaster", // Sentence Case
                color = Color.White,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 58.sp,
                    lineHeight = 54.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = Offset.Zero,
                        blurRadius = 15f
                    )
                ),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { email = it },
                placeholder = { Text("Email", color = Color.White.copy(alpha = 0.6f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedBorderColor = Color.White, // Borde blanco sólido al escribir
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f), // Borde suave al estar quieto
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            if (!isRegistering) {
                TextButton(
                    onClick = { showResetPasswordDialog = true },
                    modifier = Modifier
                        .align(Alignment.End)
                        .offset(y = (-8).dp), // Subimos el texto para que "abrace" al input
                    contentPadding = PaddingValues(0.dp) // Quitamos el padding interno del botón
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp, // Aumentamos a un tamaño estándar moderno
                        fontWeight = FontWeight.SemiBold, // Un poco más de peso para que no sea "difuso"
                        style = MaterialTheme.typography.bodySmall.copy(
                            shadow = Shadow(
                                color = Color.Black,
                                blurRadius = 4f
                            ) // Sombra sutil para legibilidad
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage != null) {
                Surface(
                    color = Color(0xFFD32F2F).copy(alpha = 0.8f), // Rojo fuerte pero integrado
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .animateContentSize() // Para que aparezca con suavidad
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
            // --- BOTÓN PRINCIPAL ---
            Button(
                onClick = {
                    errorMessage = null

                    if (isRegistering) {
                        // --- MODO REGISTRO ---
                        viewModel.registerWithEmail(email, password) { error ->
                            if (error != null) {
                                errorMessage = error
                            } else {
                                // Si no hay error, mostramos el aviso de "Revisá tu mail"
                                showVerificationSent = true
                            }
                        }
                    } else {
                        // --- MODO LOGIN ---
                        viewModel.loginWithEmail(email, password) { error ->
                            if (error == null) {
                                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                                val user = auth.currentUser

                                // 1. Le pedimos a Firebase que refresque el estado del usuario
                                user?.reload()?.addOnCompleteListener { task ->
                                    if (user?.isEmailVerified == true) {
                                        // 2. Al estar verificado, la 'isFullyAuthenticated' del MainActivity
                                        // se vuelve true y te deja pasar automáticamente.
                                        errorMessage = null
                                    } else {
                                        // 3. Si sigue sin verificar, lo deslogueamos para que el userState
                                        // en MainActivity vuelva a ser null y no intente entrar.
                                        auth.signOut()
                                        errorMessage =
                                            "Tu cuenta aún no ha sido verificada. Revisá tu email."
                                    }
                                }
                            } else {
                                errorMessage = error
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Text(
                    text = if (isRegistering) "Crear cuenta" else "Ingresar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(
                    if (isRegistering) "¿Ya tenés cuenta? Iniciá sesión" else "¿Sos nuevo? Registrate",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    errorMessage = null
                    handleGoogleSignIn()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continuar con Google", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        if (showResetPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showResetPasswordDialog = false },
                containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f), // Fondo oscuro premium
                shape = RoundedCornerShape(28.dp), // Esquinas bien redondeadas estilo Material 3
                title = {
                    Text(
                        "Restablecer contraseña",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            "Ingresá tu correo y te enviaremos un enlace para que crees una nueva clave.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedTextField(
                            value = emailToReset,
                            onValueChange = { emailToReset = it },
                            placeholder = {
                                Text(
                                    "email@ejemplo.com",
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (emailToReset.isNotEmpty()) {
                                viewModel.resetPassword(emailToReset) { error ->
                                    if (error == null) {
                                        Toast.makeText(
                                            context,
                                            "📩 ¡Correo enviado! Revisa tu bandeja.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        errorMessage = error // Usamos tu lógica de error actual
                                    }
                                }
                                showResetPasswordDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Enviar enlace", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetPasswordDialog = false }) {
                        Text("Cancelar", color = Color.White.copy(alpha = 0.6f))
                    }
                }
            )
        }
        if (showVerificationSent) {
            AlertDialog(
                onDismissRequest = { showVerificationSent = false },
                containerColor = Color(0xFF1A1A1A),
                title = { Text("¡Casi listo!", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Te enviamos un link de confirmación a $email. " +
                                "Por favor, verificalo para poder ingresar a Stock Master.",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showVerificationSent = false
                            isRegistering = false // Lo mandamos al Login para que pruebe entrar
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Entendido", color = Color.Black)
                    }
                }
            )
        }
    }
}