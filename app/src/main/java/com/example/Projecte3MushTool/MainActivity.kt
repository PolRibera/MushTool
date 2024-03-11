package com.example.Projecte3MushTool

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userReference: DatabaseReference
    private var username by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        userReference = FirebaseDatabase.getInstance().getReference("Usuari")

        setContent {
            AppTheme {
                LemonadeApp(this, auth)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val uid = auth.uid

        if (uid != null) {
            updateUsername(uid)
        }
    }
    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun updateUsername(uid : String) {
        userReference.child(uid).child("username").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedUsername = snapshot.getValue(String::class.java)
                    if (!fetchedUsername.isNullOrEmpty()) {
                        username = fetchedUsername
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(applicationContext, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LemonadeApp(context: Context, auth: FirebaseAuth) {
    val uid = auth.uid
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color(0xFF6B0C0C)),
                title = { }, // No se muestra texto en el título de la TopAppBar
                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF6B0C0C)),
                        horizontalArrangement = Arrangement.SpaceBetween // Distribuye los elementos de manera uniforme en la fila
                    ) {
                        if (!uid.isNullOrEmpty()) {
                            Text(
                                username, modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.CenterVertically), color = Color.White
                            )
                            Button(
                                onClick = {
                                    auth.signOut()
                                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                                    (context as MainActivity).restartActivity() // Reiniciar MainActivity después del cierre de sesión
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .background(Color(0xFF6B0C0C))
                            ) {
                                Text("Logout", color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = {
                                    val intentOtro = Intent(context, LoginActivity::class.java)
                                    context.startActivity(intentOtro)
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                            {
                                Image(
                                    painter = painterResource(R.drawable.user_logo),
                                    contentDescription = "Descripción de la imagen",
                                    modifier = Modifier
                                        .size(30.dp, 30.dp)
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!uid.isNullOrEmpty()) {
                BottomAppBar(
                    // Usa el mismo color para la BottomAppBar
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF6B0C0C))
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly // Distribuye los elementos de manera uniforme en la fila
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f) // Asegura que esta columna ocupe el mismo espacio que las demás
                        ) {
                            Button(
                                onClick = {
                                    val intentBus = Intent(context, BusquedaActivity::class.java)
                                    context.startActivity(intentBus)
                                },
                                modifier = Modifier
                                    .background(Color(0xFF6B0C0C))
                                    .fillMaxHeight(),// Tamaño cuadrado del botón
                                // Agrega espacio alrededor del botón
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.buscar_logo), // Cambiar con tu recurso
                                    contentDescription = "Descripción de la imagen",
                                    // Hace que la imagen llene todo el espacio disponible del botón
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f) // Asegura que esta columna ocupe el mismo espacio que las demás
                        ) {
                            Button(
                                onClick = {
                                    val intentMapa = Intent(context, MapaActivity::class.java)
                                    context.startActivity(intentMapa)
                                },
                                modifier = Modifier
                                    .background(Color(0xFF6B0C0C))
                                    .fillMaxHeight(),// Tamaño cuadrado del botón
                                // Agrega espacio alrededor del botón
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.mapa_logo), // Cambiar con tu recurso
                                    contentDescription = "Descripción de la imagen",
                                    // Hace que la imagen llene todo el espacio disponible del botón
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f) // Asegura que esta columna ocupe el mismo espacio que las demás
                        ) {
                            Button(
                                onClick = {
                                    val intentLearn = Intent(context, LearnActivity::class.java)
                                    context.startActivity(intentLearn)
                                },
                                modifier = Modifier
                                    .background(Color(0xFF6B0C0C))
                                    .fillMaxHeight(),// Tamaño cuadrado del botón
                                // Agrega espacio alrededor del botón
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.laern_logo), // Cambiar con tu recurso
                                    contentDescription = "Descripción de la imagen",
                                    // Hace que la imagen llene todo el espacio disponible del botón
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f) // Asegura que esta columna ocupe el mismo espacio que las demás
                        ) {
                            Button(
                                onClick = {
                                    val intentPLats = Intent(context, PlatsActivity::class.java)
                                    context.startActivity(intentPLats)
                                },
                                modifier = Modifier
                                    .background(Color(0xFF6B0C0C))
                                    .fillMaxHeight() // Tamaño cuadrado del botón // Agrega espacio alrededor del botón
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.logo_plats), // Cambiar con tu recurso
                                    contentDescription = "Descripción de la imagen",
                                    modifier = Modifier
                                        .fillMaxSize() // Hace que la imagen llene todo el espacio disponible del botón
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uid.isNullOrEmpty()) {
                Text("Please log in to continue", modifier = Modifier.align(Alignment.Center))
            }
            // Aquí puedes colocar el contenido principal de tu aplicación
            if (!uid.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly // Distribuye los elementos de manera uniforme en la fila
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick = {
                                    val intentPLats = Intent(context, ForoActivity::class.java)
                                    context.startActivity(intentPLats)
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(240.dp, 100.dp)
                                    .background(Color(0xFF6B0C0C)),// Tamaño cuadrado del botón
                                // Agrega espacio alrededor del botón
                            ) {
                                Text("Foro") // Texto del botón
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly // Distribuye los elementos de manera uniforme en la fila
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick = {
                                    val intentPLats =
                                        Intent(context, ListarPostsActivity::class.java)
                                    context.startActivity(intentPLats)
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(240.dp, 100.dp)
                                    .background(Color(0xFF6B0C0C)),// Tamaño cuadrado del botón
                                // Agrega espacio alrededor del botón
                            ) {
                                Text("Posts") // Texto del botón
                            }
                        }
                    }
                }
            }
        }
    }
    }
}




