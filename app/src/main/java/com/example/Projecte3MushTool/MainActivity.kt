package com.example.lemonade

import android.os.Bundle
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                LemonadeApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LemonadeApp() { // Define el color de los botones aquí

    Scaffold(
        topBar = {
            // Define tu TopBar aquí
            TopAppBar(
                modifier = Modifier.background(Color(0xFF6B0C0C)),
                title = { }, // No se muestra texto en el título de la TopAppBar
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color(0xFF6B0C0C)),
                        horizontalArrangement = Arrangement.SpaceBetween // Distribuye los elementos de manera uniforme en la fila
                    ) {
                        Text("Mushtool", modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically), color = Color.White) // Texto que se muestra en la esquina izquierda // Texto que se muestra en la esquina izquierda

                        Button(
                            onClick = { /* TODO: Acción del botón */ },
                            modifier = Modifier
                                 // Tamaño del botón
                                .align(Alignment.CenterVertically)
                                .background(Color(0xFF6B0C0C))
                        ) {
                            Image(
                                painter = painterResource(R.drawable.config), // Cambiar con tu recurso
                                contentDescription = "Descripción de la imagen",
                                modifier = Modifier
                                    .size(30.dp, 30.dp) // Tamaño de la imagen
                                // Hace que la imagen llene todo el espacio disponible del botón
                            )
                        }
                    }
                }
            )
        },
        bottomBar = { // Define tu BottomBar aquí
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
                            onClick = { /* TODO: Acción del botón */ },
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
                            onClick = { /* TODO: Acción del botón */ },
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
                            onClick = { /* TODO: Acción del botón */ },
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
                            onClick = { /* TODO: Acción del botón */ },
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
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Aquí puedes colocar el contenido principal de tu aplicación

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
                            onClick = { /* TODO: Acción del botón */ },
                            modifier = Modifier
                                .padding(10.dp)
                                .size(240.dp, 100.dp)
                                .background(Color(0xFF6B0C0C))
                            ,// Tamaño cuadrado del botón
                            // Agrega espacio alrededor del botón
                        ) {
                            Text("Mensajes") // Texto del botón
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly // Distribuye los elementos de manera uniforme en la fila
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { /* TODO: Acción del botón */ },
                            modifier = Modifier
                                .padding(10.dp)
                                .size(240.dp, 100.dp)
                                .background(Color(0xFF6B0C0C))
                            ,// Tamaño cuadrado del botón
                            // Agrega espacio alrededor del botón
                        ) {
                            Text("Fotos Comunitat") // Texto del botón
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        LemonadeApp()
    }
}