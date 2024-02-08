package com.example.Projecte3MushTool

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import androidx.compose.foundation.layout.Column as Column1

class LearnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                TestGameApp(this)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun TestGameApp(context: Context) {
        val setas = listOf("Seta 1", "Seta 2", "Seta 3", "Seta 4")
        var correctSeta by remember { mutableStateOf((0..3).random()) }
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
                                onClick = {
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    // Tamaño del botón
                                    .align(Alignment.CenterVertically)
                                    .background(Color(0xFF6B0C0C))
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.boton_exit), // Cambiar con tu recurso
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

            ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column1(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("¿Cuál es la seta comestible?")

                    setas.forEachIndexed { index, seta ->
                        Button(
                            onClick = {
                                if (index == correctSeta) {
                                    Toast.makeText(context, "¡Correcto!", Toast.LENGTH_SHORT).show()
                                    correctSeta = (0..3).random()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Incorrecto, inténtalo de nuevo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (index == correctSeta) Color.Green else Color.Gray)
                        ) {
                            Text(seta, color = Color.White)
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
            TestGameApp(LocalContext.current)
        }
    }
}
