package com.example.Projecte3MushTool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class LearnActivity : ComponentActivity() {
    private lateinit var Boletreference: DatabaseReference
    private var setasGroupedByDifficulty: Map<Int, List<Seta>> = emptyMap()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Boletreference = FirebaseDatabase.getInstance().getReference("Bolet")

        setContent {
            AppTheme {
                TestGameApp(this)
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TestGameApp(context: Context) {
        var setas by remember { mutableStateOf<List<Seta>>(emptyList()) }
        var currentQuestionIndex by remember { mutableStateOf(0) }
        var score by remember { mutableStateOf(0) }
        var desiredDifficulty by remember { mutableStateOf(1) }
        var tiempoRestante by remember { mutableStateOf(5000L) } // Tiempo inicial en milisegundos (5 segundos)
        var showTimer by remember { mutableStateOf("Tiempo restante: ${tiempoRestante / 1000} s") }
        var options by remember { mutableStateOf<List<String>>(emptyList()) }
        var correctOption by remember { mutableStateOf("") }

        val countDownTimer = object: CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = millisUntilFinished
                showTimer = "Tiempo restante: ${tiempoRestante / 1000} s"
            }

            override fun onFinish() {
                // The counter reached zero, treat as a failed response
                val intent = Intent(context, ScoreBoardActivity::class.java)
                intent.putExtra("score", score)
                context.startActivity(intent)
            }
        }
        fun startMainActivityWithUid(uid: String) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("uid", uid)
            }
            startActivity(intent)
            finish()
        }


        LaunchedEffect(true) {
            Boletreference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val setasDifficulty1 = mutableListOf<Seta>()
                    val setasDifficulty2 = mutableListOf<Seta>()
                    val setasDifficulty3 = mutableListOf<Seta>()

                    for (setaSnapshot in dataSnapshot.children) {
                        val imageUrl = setaSnapshot.child("imageUrl").getValue(String::class.java)
                        val name = setaSnapshot.child("name").getValue(String::class.java)
                        val sci_name = setaSnapshot.child("sci_name").getValue(String::class.java)
                        val warn_level = setaSnapshot.child("warn_level").getValue(Int::class.java)
                        val difficulty = setaSnapshot.child("difficulty").getValue(Int::class.java)

                        if (imageUrl != null && name != null && sci_name != null && warn_level != null && difficulty != null) {
                            val seta = Seta(imageUrl, name, sci_name, warn_level, difficulty)
                            when (difficulty) {
                                1 -> setasDifficulty1.add(seta)
                                2 -> setasDifficulty2.add(seta)
                                3 -> setasDifficulty3.add(seta)
                            }
                        }
                    }

                    // Shuffle the setas inside each difficulty group
                    setasDifficulty1.shuffle()
                    setasDifficulty2.shuffle()
                    setasDifficulty3.shuffle()

                    // Assign the shuffled and grouped setas lists to the state variable
                    setasGroupedByDifficulty = mapOf(
                        1 to setasDifficulty1,
                        2 to setasDifficulty2,
                        3 to setasDifficulty3
                    )

                    // Update the setas list with the setas of the desired difficulty
                    setas = setasGroupedByDifficulty[desiredDifficulty] ?: emptyList()

                    // Shuffle options only when a new question is presented
                    options = setas.map { it.name }.shuffled()
                    correctOption = setas[currentQuestionIndex].name
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }

        DisposableEffect(Unit) {
            countDownTimer.start()

            onDispose {
                countDownTimer.cancel()
            }
        }

        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                (TopAppBar(
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
                                    auth.uid?.let { startMainActivityWithUid(it) }
                                },
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
                ))
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(modifier = Modifier.padding(16.dp)) {

                    if (setas.isEmpty()) {
                        Text("Cargando cuestionario...")

                    } else if (currentQuestionIndex < setas.size) {
                        Text(showTimer)
                        val seta = setas[currentQuestionIndex]

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
                                    score++
                                    currentQuestionIndex++

                                    if (currentQuestionIndex >= setas.size) {
                                        if (desiredDifficulty < 3) {
                                            desiredDifficulty++
                                            setas = setasGroupedByDifficulty[desiredDifficulty] ?: emptyList()
                                            currentQuestionIndex = 0
                                        } else {
                                            currentQuestionIndex = setas.size
                                        }
                                    }
                                } else {
                                    val intent = Intent(context, ScoreBoardActivity::class.java)
                                    intent.putExtra("score", score)
                                    context.startActivity(intent)
                                }
                            }
                        )
                    } else {
                        val intent = Intent(context, ScoreBoardActivity::class.java)
                        intent.putExtra("score", score)
                        context.startActivity(intent)
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
    ){
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
             TestGameApp(LocalContext.current) // You may need to adjust this line
        }
    }
}
