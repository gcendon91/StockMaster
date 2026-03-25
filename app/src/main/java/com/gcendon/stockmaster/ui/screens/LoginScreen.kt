package com.gcendon.stockmaster.ui.screens

// ESTOS SON LOS CRÍTICOS (Fijate que todos dicen ANDROIDX)
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.gcendon.stockmaster.ui.viewmodel.ProductViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun LoginScreen(viewModel: ProductViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val webClientId = "776569543507-kackuq8mm4lijob2kj9c3k20lhc476hj.apps.googleusercontent.com"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Stock Master", color = Color.White, style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()

                val request =
                    GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

                coroutineScope.launch {
                    try {
                        val result = credentialManager.getCredential(
                            context = context, request = request
                        )
                        val credential = result.credential

                        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                            viewModel.signInWithGoogle(googleIdToken.idToken)
                        }
                    } catch (e: Exception) {
                        Log.e("LOGIN", "Error: ${e.message}")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White, contentColor = Color.Black
            )
        ) {
            Text("Continuar con Google", fontWeight = FontWeight.Bold)
        }
    }
}