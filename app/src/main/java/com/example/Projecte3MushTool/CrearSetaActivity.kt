package com.example.Projecte3MushTool

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import android.Manifest
import android.content.ClipDescription
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
import androidx.compose.material3.ButtonDefaults
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class CrearSetaActivity : ComponentActivity() {
    private lateinit var Boletreference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUriPre: Uri? = null
    private var imageUrl: Uri? = null
    var name by mutableStateOf("")
    var sciName by mutableStateOf("")
    var warnLevel by mutableStateOf("")
    var difficulty by mutableStateOf("")

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Boletreference = FirebaseDatabase.getInstance().getReference("Bolet")
        storageReference = FirebaseStorage.getInstance().reference

        setContent {
            CrearSetaApp(this)
        }

        // Solicitar permisos de almacenamiento al iniciar la actividad
        requestStoragePermission()
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, imageName: String) {
        val imageRef = storageReference.child("images").child("$imageName.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
                    imageUrl = uri
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error al subir la imagen: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
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

    fun crearNuevaSeta(
        img_path: String,
        name: String,
        sci_name: String,
        warn_level: Int,
        difficulty: Int,
        description: String
    ) {
        val seta = Seta(img_path, name, sci_name, warn_level, difficulty, description)
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
        var name by remember { mutableStateOf("") }
        var sciName by remember { mutableStateOf("") }
        var warnLevel by remember { mutableStateOf("") }
        var difficulty by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

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
                                colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
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
                        label = { Text("Nombre de la seta") }
                    )

                    TextField(
                        value = sciName,
                        onValueChange = { sciName = it },
                        label = { Text("Nombre científico de la seta") }
                    )

                    TextField(
                        value = warnLevel,
                        onValueChange = { warnLevel = it },
                        label = { Text("Nivel de advertencia (0-10)") }
                    )

                    TextField(
                        value = difficulty,
                        onValueChange = { difficulty = it },
                        label = {
                            Text("Dificultad (0-10)")
                        }
                    )
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = {
                            Text("Descripción de la seta")
                        }
                    )
                    Button(


                        onClick = {
                            // Abrir la galería
                            val intent = Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityForResult(intent, PICK_IMAGE_REQUEST)

                        },colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color(0xFF6B0C0C))
                    ) {
                        Text("Seleccionar imagen")
                    }
                    imageUriPre?.let { uri ->
                        // Si hay una URI de imagen seleccionada, la cargamos
                        imageUrl?.let { imageUrl ->
                            Text(
                                "Imagen seleccionada: $imageUrl"
                            )
                        } ?: run {
                            // Si no hay URI de imagen seleccionada, mostramos un mensaje
                            Text("No se ha seleccionado ninguna imagen")
                        }
                    }

                    Button(
                        onClick = {
                            // Crear la seta si hay una imagen seleccionada
                            crearNuevaSeta(
                                imageUrl.toString(),
                                name,
                                sciName,
                                warnLevel.toInt(),
                                difficulty.toInt(),
                                description
                            )
                            val intent = Intent(context, BusquedaActivity::class.java)
                            context.startActivity(intent)
                        }, colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),modifier = Modifier
                            .padding(16.dp)
                            .background(Color(0xFF6B0C0C))
                    ) {
                        Text("Crear seta")
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            imageUri?.let {
                // Llama a la función para subir la imagen a Firebase Storage
                uploadImageToFirebaseStorage(it, UUID.randomUUID().toString())
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
