package com.example.Projecte3MushTool

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.google.android.gms.location.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*
import android.Manifest
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import com.example.lemonade.ui.theme.AppTheme
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task




class PostActivity : ComponentActivity() {
    private lateinit var postReference: DatabaseReference
    private lateinit var boletReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var imageUriPre: Uri? = null
    private var imageUrl: Uri? = null
    private var lastKnownLocation: Location? = null
    private var comentario by mutableStateOf("")
    var selectedSeta by mutableStateOf<Seta?>(null)

    companion object {
        private const val CAMERA_REQUEST_CODE = 1888
        private const val PERMISSION_REQUEST_CODE = 123
        private const val LOCATION_PERMISSION_REQUEST_CODE = 456
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postReference = FirebaseDatabase.getInstance().getReference("Post")
        boletReference = FirebaseDatabase.getInstance().getReference("Bolet")
        storageReference = FirebaseStorage.getInstance().reference
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            CrearPostApp(this)
        }

        // Solicitar permisos de almacenamiento y ubicación al iniciar la actividad
        requestStoragePermission()
        requestLocationPermission()
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

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            imageBitmap?.let {
                // Convert Bitmap to Uri
                imageUriPre = getImageUriFromBitmap(it)
                // Obtain Latitude and Longitude here (for example, sample values 0.0 and 0.0)
                // Upload Image to Firebase Storage
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

    private fun obtenerUbicacionActual(context: Context, onLocationSuccess: (Location) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        onLocationSuccess(it)
                    } ?: run {
                        Toast.makeText(
                            context,
                            "No se pudo obtener la ubicación actual",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al obtener la ubicación: ${e.message}")
                }
        }
    }
    private fun crearNuevoPost(imgPath: String, comentario: String, sciNameSeta: String, context: Context) {
        obtenerUbicacionActual(context) { location ->
            val locationString = "${location.latitude};${location.longitude}"

            // Crea el objeto Post con la imagen, el comentario, la ubicación y otros detalles necesarios
            val post = Post(imgPath, comentario, sciNameSeta, locationString)

            // Guarda el post en la base de datos Firebase
            postReference.push().setValue(post)
                .addOnSuccessListener {
                    Toast.makeText(context, "Post añadido correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al añadir post", Toast.LENGTH_SHORT).show()
                }
        }

    }




    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearPostApp(context: Context) {
        var setasState by remember { mutableStateOf<List<Seta>>(emptyList()) }
        var isDialogOpen by remember { mutableStateOf(false) }
        var selectedSetaName by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(true) {
            val setas = mutableListOf<Seta>()
            boletReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val newSetas = mutableListOf<Seta>()

                    for (setaSnapshot in dataSnapshot.children) {
                        val imageUrl =
                            setaSnapshot.child("imageUrl").getValue(String::class.java)
                        val name = setaSnapshot.child("name").getValue(String::class.java)
                        val sci_name =
                            setaSnapshot.child("sci_name").getValue(String::class.java)
                        val warn_level =
                            setaSnapshot.child("warn_level").getValue(Int::class.java)
                        val difficulty =
                            setaSnapshot.child("difficulty").getValue(Int::class.java)

                        if (name != null && sci_name != null && warn_level != null && difficulty != null && imageUrl != null) {
                            val seta = Seta(imageUrl, name, sci_name, warn_level, difficulty)
                            newSetas.add(seta)
                        }
                    }
                    setasState = newSetas
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancellation
                }
            })
        }

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
                                colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
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
                    Button(
                        onClick = {
                            launchCamera()
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF6B0C0C))

                    ) {
                        Text("Tomar Foto")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
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

                    Button(
                        onClick = {
                            isDialogOpen = true
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF6B0C0C))
                    ) {
                        Text("Seleccionar Setas")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    selectedSetaName?.let { selectedSetaName ->
                        Text(
                            text = "Seta seleccionada: $selectedSetaName",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Show Captured Image

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text Field for Comment
                    TextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentario Post")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to Post Comment
                    Button(
                        onClick = {
                            // Upload Image, Comment, and Selected Mushroom to Firebase
                            imageUrl?.let {
                                selectedSeta?.let { seta ->
                                    crearNuevoPost(it.toString(), comentario, seta.sci_name, context)
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF6B0C0C))
                    ) {
                        Text("Post")
                    }
                }
            }
        }
        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text("Seleccionar Seta") },
                text = {
                    LazyColumn {
                        items(setasState) { seta ->
                            Text(
                                text = seta.name,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        selectedSetaName = seta.name
                                        selectedSeta = seta
                                        isDialogOpen = false
                                    }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { isDialogOpen = false },
                        colors = ButtonDefaults.buttonColors( Color(0xFF6B0C0C))
                    ) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppTheme {
            CrearPostApp(LocalContext.current)
        }
    }
}
