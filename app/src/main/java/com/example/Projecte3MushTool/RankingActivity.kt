package com.example.Projecte3MushTool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RankingActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userReference: DatabaseReference
    private var username by mutableStateOf("")
    private var scores by mutableStateOf<List<ScoreQuiz>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        userReference = FirebaseDatabase.getInstance().getReference("Usuari")

        setContent {
            AppTheme {
                RankingApp(this, auth)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val uid = auth.uid

        if (uid != null) {
            updateUsername(uid)
            loadScores()
        }
    }

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun updateUsername(uid: String) {
        userReference.child(uid).child("username").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedUsername = snapshot.getValue(String::class.java)
                    if (!fetchedUsername.isNullOrEmpty()) {
                        username = fetchedUsername
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(applicationContext, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun loadScores() {
        val scoresReference = FirebaseDatabase.getInstance().getReference("scores")
        scoresReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scoreList = mutableListOf<ScoreQuiz>()
                for (scoreSnapshot in snapshot.children) {
                    val score = scoreSnapshot.getValue(ScoreQuiz::class.java)
                    score?.let {
                        scoreList.add(it)
                    }
                }
                scores = scoreList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(applicationContext, "Error fetching scores", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RankingApp(context: Context, auth: FirebaseAuth) {
        Scaffold(
            topBar = {
                TopAppBar(
                    {
                        Text(text = "Ranking")
                    }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    scores.forEach { score ->
                        ScoreItem(score = score)
                    }
                }
            }
        )
    }

    @Composable
    fun ScoreItem(score: ScoreQuiz) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Username: ${score.username}")
            Text(text = "Score: ${score.score}")
        }
    }
}
