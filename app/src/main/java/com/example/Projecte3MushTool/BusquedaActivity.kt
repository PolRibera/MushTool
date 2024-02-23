package com.example.Projecte3MushTool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.database.*
import coil.compose.rememberImagePainter
import com.google.firebase.storage.FirebaseStorage

class BusquedaActivity : ComponentActivity() {
    private lateinit var Boletreference: DatabaseReference
    private lateinit var storageReference: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        storageReference = FirebaseStorage.getInstance()
        Boletreference = FirebaseDatabase.getInstance().getReference("Bolet")

        setContent {
            AppTheme {
                BusquedaApp(this)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BusquedaApp(context: Context) {
        var setasState by remember { mutableStateOf<List<Seta>>(emptyList()) }
        var selectedSeta by remember { mutableStateOf<Seta?>(null) }
        var showDialog by remember { mutableStateOf(false) }

        LaunchedEffect(true) {

            Boletreference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val newSetas = mutableListOf<Seta>()

                    for (setaSnapshot in dataSnapshot.children) {
                        val imageUrl = setaSnapshot.child("imageUrl").getValue(String::class.java)
                        val name = setaSnapshot.child("name").getValue(String::class.java)
                        val sci_name = setaSnapshot.child("sci_name").getValue(String::class.java)
                        val warn_level = setaSnapshot.child("warn_level").getValue(Int::class.java)
                        val difficulty = setaSnapshot.child("difficulty").getValue(Int::class.java)

                        if (name != null && sci_name != null && warn_level != null && difficulty != null && imageUrl != null) {
                            val seta = Seta(imageUrl,name, sci_name, warn_level, difficulty)
                            newSetas.add(seta)
                        }
                    }
                    setasState = newSetas
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
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
                            val intent = Intent(context, CrearSetaActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .background(Color(0xFF6B0C0C))
                    ) {
                        Text("Añadir seta")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn {
                        items(setasState) { seta ->
                            Row() {
                                Image(
                                    painter = rememberImagePainter(seta.imageUrl),
                                    contentDescription = "Imagen de ${seta.name}",
                                    modifier = Modifier.size(100.dp)
                                )
                                Column() {
                                    Text(text = "Nombre cientifico: " + seta.sci_name)
                                    Text(text = "Nombre comun: " + seta.name)
                                    Text(text = "Nivel de peligrosidad (0-10): " + seta.warn_level + "")
                                    Row {
                                        Button(onClick = {
                                            selectedSeta = seta
                                            showDialog = true
                                        }) {
                                            Text("Delete")
                                        }
                                        Button(onClick = {
                                            val intent =
                                                Intent(context, EditarSetaActivity::class.java)
                                            intent.putExtra("img_path", seta.imageUrl)
                                            intent.putExtra("name", seta.name)
                                            intent.putExtra("sci_name", seta.sci_name)
                                            intent.putExtra("warn_level", seta.warn_level)
                                            context.startActivity(intent)
                                        }) {
                                            Text("Edit")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (showDialog) {
                                        AlertDialog(
                                            onDismissRequest = {
                                                showDialog = false
                                                selectedSeta = null
                                            },
                                            title = {
                                                Text("Confirmación")
                                            },
                                            text = {
                                                Text("¿Seguro que quieres borrar esta seta?")
                                            },
                                            confirmButton = {
                                                Button(
                                                    onClick = {
                                                        showDialog = false
                                                        selectedSeta?.let { seta ->
                                                            // Remove seta from database
                                                            Boletreference.child(seta.sci_name)
                                                                .removeValue()
                                                            Toast.makeText(
                                                                context,
                                                                "Seta eliminada correctamente",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            selectedSeta = null
                                                        }
                                                    }
                                                ) {
                                                    Text("Confirmar")
                                                }
                                            },
                                            dismissButton = {
                                                Button(
                                                    onClick = {
                                                        showDialog = false
                                                        selectedSeta = null
                                                    }
                                                ) {
                                                    Text("Cancelar")
                                                }
                                            }
                                        )
                                    }
                                }
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
            BusquedaApp(LocalContext.current)
        }
    }
}
