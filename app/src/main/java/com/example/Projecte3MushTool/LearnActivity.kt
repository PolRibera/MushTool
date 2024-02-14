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
import coil.compose.rememberImagePainter
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.database.*

class LearnActivity : ComponentActivity() {
    private lateinit var Boletreference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Boletreference = FirebaseDatabase.getInstance().getReference("Bolet")

        setContent {
            AppTheme {
                TestGameApp(this)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TestGameApp(context: Context) {
        var setas by remember { mutableStateOf<List<Seta>>(emptyList()) }
        var correctSetaIndex by remember { mutableStateOf(0) }
        var currentQuestionIndex by remember { mutableStateOf(0) }

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

                        if (imageUrl != null && name != null && sci_name != null && warn_level != null && difficulty != null) {
                            newSetas.add(Seta(imageUrl, name, sci_name, warn_level, difficulty))
                        }
                    }

                    setas = newSetas
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Maneja posibles errores.
                }
            })
        }

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
                                    context.startActivity(intent)
                                },
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
                    if (currentQuestionIndex < setas.size) {
                        val seta = setas[currentQuestionIndex]
                        val options = setas.map { it.name }.shuffled()
                        val correctOption = seta.name

                        Question(
                            seta = seta,
                            options = options,
                            correctOption = correctOption,
                            onOptionSelected = { selectedOption ->
                                if (selectedOption == correctOption) {
                                    Toast.makeText(
                                        context,
                                        "¡Correcto!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    currentQuestionIndex++
                                    correctSetaIndex = (0 until setas.size).random()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Incorrecto, inténtalo de nuevo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    } else {
                        // Todas las preguntas han sido respondidas
                        Text("¡Felicidades, has respondido todas las preguntas!")
                    }
                }
            }
        }
    }

    @Composable
    fun Question(
        seta: Seta,
        options: List<String>,
        correctOption: String,
        onOptionSelected: (String) -> Unit
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberImagePainter(seta.imageUrl),
                contentDescription = seta.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            options.forEach { option ->
                Button(
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = option)
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