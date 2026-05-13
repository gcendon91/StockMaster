package com.gcendon.stockmaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gcendon.stockmaster.data.AppUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdMembersSheet(
    onDismiss: () -> Unit,
    members: List<AppUser>,
    currentUserUid: String?,
    householdId: String?,
    onRemoveMember: (String) -> Unit,
    onLeaveHousehold: () -> Unit
) {
    var memberToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }

    val isCurrentUserAdmin = currentUserUid == householdId

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1C), // Gris muy oscuro coherente con el filtro
        contentColor = Color.White,
        tonalElevation = 16.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = "MIEMBROS DEL HOGAR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (members.isEmpty()) {
                CircularProgressIndicator(
                    color = Color(0xFF43A047),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(members) { member ->
                        // Ahora accedemos directo a las propiedades del objeto AppUser
                        val isMe = member.uid == currentUserUid
                        val isMemberAdmin = member.uid == householdId

                        val canDeleteThisMember = isCurrentUserAdmin && !isMe && !isMemberAdmin

                        MemberItem(
                            name = member.displayName,
                            email = member.email,
                            photoUrl = member.photoUrl,
                            isMe = isMe,
                            isAdmin = isMemberAdmin, // Pasamos este dato para ponerle un "título" visual
                            showDeleteButton = canDeleteThisMember, // Nuevo parámetro en tu MemberItem
                            onRemove = { memberToDelete = member.uid to member.displayName })
                    }
                }
            }
        }
    }
    if (memberToDelete != null) {
        AlertDialog(
            onDismissRequest = { memberToDelete = null }, // Si toca afuera, se cierra
            containerColor = Color(0xFF2C2C2C), // Un gris un poquito más claro que el fondo
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f),
            title = {
                Text(
                    "¿Eliminar miembro?", fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Estás seguro de que querés eliminar a ${memberToDelete?.second} del hogar? Perderá acceso al stock compartido.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveMember(memberToDelete!!.first)
                        memberToDelete = null // Cerramos el diálogo después de borrar
                    }) {
                    Text("ELIMINAR", color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToDelete = null }) {
                    Text("CANCELAR", color = Color.White)
                }
            })
    }
    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            containerColor = Color(0xFF2C2C2C),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f),
            title = { Text("¿Salir del hogar?") },
            text = { Text("Dejarás de ver el inventario compartido. Volverás a tu propio hogar.") },
            confirmButton = {
                TextButton(onClick = {
                    onLeaveHousehold()
                    showLeaveConfirmation = false
                    onDismiss() // Cerramos el sheet
                }) {
                    Text("SALIR", color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirmation = false }) {
                    Text("CANCELAR", color = Color.White)
                }
            })
    }
}


@Composable
fun MemberItem(
    name: String,
    email: String,
    photoUrl: String?,
    isMe: Boolean,
    isAdmin: Boolean,           // <--- Nuevo parámetro
    showDeleteButton: Boolean,  // <--- Nuevo parámetro
    onRemove: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto de Perfil Mini
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.1f)
            ) {
                if (photoUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Lógica de nombre para que quede bien claro
                val displayTitle = buildString {
                    append(name)
                    if (isMe) append(" (Vos)")
                    if (isAdmin) append(" 👑") // Podés cambiarlo por " (Dueño)" si preferís sin emojis
                }

                Text(
                    text = displayTitle,
                    fontWeight = FontWeight.Bold,
                    color = if (isAdmin) Color(0xFF43A047) else Color.White // Resaltamos al dueño en verde
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // ACÁ USAMOS LA VARIABLE PARA MOSTRAR U OCULTAR EL BOTÓN
            if (showDeleteButton) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.background(Color.Red.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.PersonRemove,
                        null,
                        tint = Color(0xFFFF8A80),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}