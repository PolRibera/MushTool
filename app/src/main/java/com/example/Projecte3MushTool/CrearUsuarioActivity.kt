package com.example.Projecte3MushTool

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CrearUsuarioActivity : ComponentActivity() {
    private lateinit var userReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        userReference = FirebaseDatabase.getInstance().getReference("Users")

        setContent {
            CrearUsuarioApp(this)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de almacenamiento concedido", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun crearNuevoUsuario(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val user = User(userId, name, email, password)
                    userReference.child(userId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, BusquedaActivity::class.java)
                            startActivity(intent)
                        }.addOnFailureListener {
                            Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error al crear el usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PERMISSION_REQUEST_CODE = 123
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearUsuarioApp(context: Context) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    modifier = Modifier.background(Color(0xFF6B0C0C)),
                    title = { },
                    actions = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF6B0C0C)),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Mushtool",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.CenterVertically),
                                color = Color.White
                            )

                            Button(
                                onClick = {
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .background(Color(0xFF6B0C0C))
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.boton_exit),
                                    contentDescription = "Exit Button",
                                    modifier = Modifier
                                        .size(30.dp, 30.dp)
                                        .align(Alignment.CenterVertically)
                                        .background(Color(0xFF6B0C0C))
                                )
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") }
                    )

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") }
                    )

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") }
                    )

                    Button(
                        onClick = {
                            crearNuevoUsuario(name, email, password)
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color(0xFF6B0C0C))
                    ) {
                        Text("Crear Usuario")
                    }
                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppTheme {
            CrearUsuarioApp(LocalContext.current)
        }
    }
}
