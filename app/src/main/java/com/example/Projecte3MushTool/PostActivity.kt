package com.example.Projecte3MushTool

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import coil.compose.rememberImagePainter
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*




class PostActivity : ComponentActivity() {
    private lateinit var Boletreference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUriPre: Uri? = null
    private var imageUrl: Uri? = null
    var id by mutableStateOf("")
    var comentario by mutableStateOf("")

    companion object {
        private const val CAMERA_REQUEST_CODE = 1888
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Boletreference = FirebaseDatabase.getInstance().getReference("Post")
        storageReference = FirebaseStorage.getInstance().reference

        setContent {
            CrearSetaApp(this)
        }

        // Solicitar permisos de almacenamiento al iniciar la actividad
        requestStoragePermission()
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun launchCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
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
                Toast.makeText(
                    this,
                    "Permiso de almacenamiento concedido",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Permiso de almacenamiento denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun crearNuevoPost(img_path: String, comentario: String) {
        val post = Post(img_path, comentario)
        Boletreference.push().setValue(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post añadido correctamente", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al añadir post", Toast.LENGTH_SHORT).show()
            }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearSetaApp(context: Context) {
        var id by remember { mutableStateOf("") }
        var comentario by remember { mutableStateOf("") }
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
                    Button(
                        onClick = {
                            // Abrir la cámara
                            launchCamera()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Tomar Foto")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar la imagen capturada
                    imageUriPre?.let { uri ->
                        Image(
                            painter = rememberImagePainter(uri),
                            contentDescription = "Captured Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cuadro de texto para el comentario
                    TextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentario Post") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para publicar el comentario
                    Button(
                        onClick = {
                            // Subir la imagen y el comentario a Firebase
                            imageUrl?.let {
                                crearNuevoPost(it.toString(), comentario)
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Post")
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            imageBitmap?.let {
                // Convertir el Bitmap en una Uri
                imageUriPre = getImageUriFromBitmap(it)
                // Subir la imagen a Firebase Storage
                imageUriPre?.let { uri ->
                    uploadImageToFirebaseStorage(uri, UUID.randomUUID().toString())
                }
            }
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppTheme {
            CrearSetaApp(LocalContext.current)
        }
    }
}
