package com.example.Projecte3MushTool

import androidx.compose.runtime.Composable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditarUsuarioActivity : ComponentActivity() {
    private lateinit var userReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        userReference = FirebaseDatabase.getInstance().getReference("Users")
        auth = Firebase.auth

        val userId = intent.getStringExtra("user_id")
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        setContent {
            EditarUsuarioApp(this, userId, name, email)
        }
    }

    @Composable
    fun EditarUsuarioApp(context: Context, userId: String?, name: String?, email: String?) {
        val nameState = remember { mutableStateOf(name ?: "") }
        val emailState = remember { mutableStateOf(email ?: "") }

        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Editar Usuario") }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = { Text("Nombre") }
                    )
                    OutlinedTextField(
                        value = emailState.value,
                        onValueChange = { emailState.value = it },
                        label = { Text("Correo electrónico") }
                    )
                    Button(
                        onClick = {
                            val newName = nameState.value
                            val newEmail = emailState.value

                            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                                userId?.let { uid ->
                                    val userRef = userReference.child(uid)
                                    userRef.child("name").setValue(newName)
                                    userRef.child("email").setValue(newEmail)

                                    Toast.makeText(
                                        context,
                                        "Usuario actualizado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(context, UserProfileActivity::class.java)
                                    intent.putExtra("user_id", userId)
                                    context.startActivity(intent)
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Nombre y correo electrónico son campos obligatorios",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppTheme {
            EditarUsuarioApp(LocalContext.current, "userID123", "John Doe", "johndoe@example.com")
        }
    }
}
