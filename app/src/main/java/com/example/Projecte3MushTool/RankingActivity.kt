package com.example.Projecte3MushTool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RankingActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var scoresReference: DatabaseReference
    private var scores by mutableStateOf<List<ScoreQuiz>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        scoresReference = FirebaseDatabase.getInstance().getReference("scores")

        setContent {
            AppTheme {
                RankingApp(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadScores()
    }

    private fun loadScores() {
        scoresReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scoreList = mutableListOf<ScoreQuiz>()
                for (scoreSnapshot in snapshot.children) {
                    val score = scoreSnapshot.getValue(ScoreQuiz::class.java)
                    score?.let {
                        scoreList.add(it)
                    }
                }
                scores = scoreList.sortedByDescending { it.score }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun RankingApp(context: Context) {
        Scaffold(
            topBar = {
                TopAppBar(
                    {
                        Text(text = "Ranking")
                    }
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        ScoreTable(scores = scores)
                    }
                }
            }
        )
    }

    @Composable
    fun ScoreTable(scores: List<ScoreQuiz>) {
        Column {
            // Header
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Usuario")
                Text(text = "Puntuación")
            }

            // Scores
            scores.forEach { scoreQuiz ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = scoreQuiz.username)
                    Text(text = scoreQuiz.score.toString())
                }
            }
        }
    }
}