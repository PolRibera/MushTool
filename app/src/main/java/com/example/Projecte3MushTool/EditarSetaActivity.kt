package com.example.Projecte3MushTool

import androidx.compose.runtime.Composable



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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

class EditarSetaActivity : ComponentActivity() {
    private lateinit var Boletreference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        Boletreference = FirebaseDatabase.getInstance().getReference("Bolet")

        val imgPath = intent.getStringExtra("img_path")
        val name = intent.getStringExtra("name")
        val sciName = intent.getStringExtra("sci_name")
        val warnLevel = intent.getIntExtra("warn_level", 0)

        setContent {
                EditarSetaApp(this, imgPath, name, sciName, warnLevel)
        }
    }

    @Composable
    fun EditarSetaApp(context: Context, imgPath: String?, name: String?, sciName: String?, warnLevel: Int?) {
        val imgPathState = remember { mutableStateOf(imgPath ?: "") }
        val nameState = remember { mutableStateOf(name ?: "") }
        val sciNameState = remember { mutableStateOf(sciName ?: "") }
        val warnLevelState = remember { mutableStateOf(warnLevel?.toString() ?: "") }


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
                    OutlinedTextField(
                        value = imgPathState.value,
                        onValueChange = { imgPathState.value = it },
                        label = { Text("Ruta de la imagen") }
                    )
                    OutlinedTextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = { Text("Nombre común") }
                    )
                    OutlinedTextField(
                        value = sciNameState.value,
                        onValueChange = { sciNameState.value = it },
                        label = { Text("Nombre científico") }
                    )
                    OutlinedTextField(
                        value = warnLevelState.value,
                        onValueChange = { warnLevelState.value = it },
                        label = { Text("Nivel de advertencia") }
                    )
                    Button(
                        onClick = {
                            val imgPath = imgPathState.value
                            val name = nameState.value
                            val sciName = sciNameState.value
                            val warnLevel = warnLevelState.value.toIntOrNull() ?: 0

                            // Obtener el ID de la seta

                            // Verificar que todos los campos requeridos estén llenos
                            if (imgPath.isNotEmpty() && name.isNotEmpty() && sciName.isNotEmpty()) {
                                val setaReference = Boletreference.child(sciName)

                                // Actualizar la seta en la base de datos
                                setaReference.child("img_path").setValue(imgPath)
                                setaReference.child("name").setValue(name)
                                setaReference.child("sci_name").setValue(sciName)
                                setaReference.child("warn_level").setValue(warnLevel)

                                // Mensaje de éxito
                                Toast.makeText(
                                    context,
                                    "Seta actualizada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(context, BusquedaActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                // Mensaje de error si algún campo está vacío
                                Toast.makeText(
                                    context,
                                    "Todos los campos son obligatorios",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    ) {
                        Text("Actualizar seta")
                    }
                }
            }

        }
    }

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        LemonadeApp(LocalContext.current)
        }
    }
}