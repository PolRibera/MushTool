package com.example.Projecte3MushTool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.remember
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CrearSetaActivity : ComponentActivity() {
    private lateinit var Boletreference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        Boletreference = FirebaseDatabase.getInstance().getReference("Bolet")

        setContent {
            AppTheme {
                CrearSetaApp(this)
            }
        }
    }

    // Esta función crea una nueva seta en la base de datos
    fun crearNuevaSeta(img_path: String, name: String, sci_name: String, warn_level: Int) {
        val seta = Seta(img_path, name, sci_name, warn_level)
        Boletreference.child(sci_name).setValue(seta)
            .addOnSuccessListener {
            Toast.makeText(this, "Seta añadida correctamente", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al añadir la seta", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearSetaApp(context: Context) {
        val imgPathState =  remember { mutableStateOf("") }
        val nameState =  remember { mutableStateOf("") }
        val sciNameState =  remember { mutableStateOf("") }
        val warnLevelState = remember  { mutableStateOf(0) }

        Scaffold(
            topBar = {
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
                                    context.startActivity(intent) },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .background(Color(0xFF6B0C0C))
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.boton_exit),
                                    contentDescription = "Exit Button",
                                    modifier = Modifier.size(30.dp, 30.dp)
                                )
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Campo de entrada para la imagen de la seta
                    // Aquí puedes implementar la lógica para seleccionar la imagen
                    // por medio de un diálogo de selección de imágenes, etc.
                    // Actualmente es un campo de texto simple.
                    TextField(
                        value = imgPathState.value,
                        onValueChange = { imgPathState.value = it },
                        label = { Text("Ruta de la imagen") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de entrada para el nombre de la seta
                    TextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = { Text("Nombre de la seta") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de entrada para el nombre científico de la seta
                    TextField(
                        value = sciNameState.value,
                        onValueChange = { sciNameState.value = it },
                        label = { Text("Nombre científico de la seta") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de entrada para el nivel de advertencia de la seta
                    // Aquí puedes usar un control deslizante u otro widget apropiado
                    // dependiendo de cómo quieras manejar el nivel de advertencia.
                    // Este ejemplo usa un campo de entrada de texto simple.
                    TextField(
                        value = warnLevelState.value.toString(),
                        onValueChange = {
                            // Manejar la conversión de String a Int de manera segura
                            warnLevelState.value = it.toIntOrNull() ?: 0
                        },
                        label = { Text("Nivel de advertencia (0-3)") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para enviar el formulario y crear la nueva seta
                    Button(
                        onClick = {
                            // Llamar a la función para crear una nueva seta
                            crearNuevaSeta(imgPathState.value, nameState.value, sciNameState.value, warnLevelState.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF6B0C0C))
                    ) {
                        Text("Crear nueva seta", color = Color.White)
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppTheme {
            CrearSetaApp(LocalContext.current)
        }
    }
}