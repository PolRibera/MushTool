package com.example.Projecte3MushTool

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditForoPostActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val editTextPostState = mutableStateOf("")

        postId = intent.getStringExtra("postId") ?: ""

        setContent {
            AppTheme {
                EditForoPostActivityView(this, auth, postId, editTextPostState)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditForoPostActivityView(
        context: Context,
        auth: FirebaseAuth,
        postId: String,
        editTextPost: MutableState<String>
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.background(Color(0xFF6B0C0C)),
                    title = { }, // No se muestra texto en el título de la TopAppBar
                    navigationIcon = {
                        IconButton(onClick = { onBackPressed() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF6B0C0C)),
                            horizontalArrangement = Arrangement.SpaceBetween // Distribuye los elementos de manera uniforme en la fila
                        ) {
                            Text(
                                "Editar Post",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.CenterVertically),
                                color = Color.White
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = editTextPost.value,
                        onValueChange = { editTextPost.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Escribe tu post aquí") }
                    )
                    Button(
                        onClick = {
                            // Guardar el texto editado del post en Firebase
                            val newText = editTextPost.value.trim()
                            if (newText.isNotEmpty()) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    val database = FirebaseDatabase.getInstance().reference.child("ForoPost")
                                    database.child(postId).child("text").setValue(newText)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Post editado exitosamente",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Si deseas cerrar la actividad después de editar el post, descomenta la siguiente línea
                                            // (context as EditForoPostActivity).finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("EditForoPostActivity", "Error al editar el post: $e")
                                        }
                                } else {
                                    Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Debes ingresar un texto", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Guardar")
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        EditForoPostActivity()
    }
}
