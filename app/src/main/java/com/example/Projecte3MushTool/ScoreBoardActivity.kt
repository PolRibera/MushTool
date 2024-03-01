package com.example.Projecte3MushTool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ScoreBoardActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val score = intent.getIntExtra("score", 0)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            ScoreBoard(score = score)
        }

        // Save the score to the database
        saveScoreToDatabase(auth.currentUser?.uid ?: "", score)
    }

    @Composable
    fun ScoreBoard(score: Int) {
        Text(text = "Score: $score")
    }

    private fun saveScoreToDatabase(uid: String, score: Int) {
        // Create an instance of ScoreQuiz
        val scoreQuiz = ScoreQuiz(uid, score)

        // Get a reference to the scores in the database
        val scoresReference = FirebaseDatabase.getInstance().getReference("scores")

        // Save the ScoreQuiz instance to the database
        scoresReference.child(uid).setValue(scoreQuiz)
    }
}