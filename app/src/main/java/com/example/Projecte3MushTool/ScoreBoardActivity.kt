package com.example.Projecte3MushTool

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.UUID

class ScoreBoardActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userReference: DatabaseReference
    private var username by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val score = intent.getIntExtra("score", 0)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        userReference = FirebaseDatabase.getInstance().getReference("Usuari")

        setContent {
            ScoreBoard(score = score)
        }
    }

    override fun onResume() {
        super.onResume()
        val uid = auth.uid

        if (uid != null) {
            updateUsername(uid)
        }
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
                }
            }
        )
    }

    @Composable
    fun ScoreBoard(score: Int) {
        val username = remember { mutableStateOf("") }

        // Actualizar el valor de username aquí
        LaunchedEffect(key1 = Unit) {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                userReference.child(uid).child("username").get().addOnSuccessListener {
                    username.value = it.getValue(String::class.java) ?: ""
                }
            }
        }

        Column {
            Text(text = "Score: $score")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                saveScoreToDatabase(auth.currentUser?.uid ?: "", score, username.value)
            }) {
                Text(text = "Save Score")
            }

            Spacer(modifier = Modifier.height(16.dp))



            Button(onClick = {
                val intent = Intent(this@ScoreBoardActivity, RankingActivity::class.java)
                startActivity(intent)
            }) {
                Text(text = "Go to RankingActivity")
            }
        }
    }

    private fun saveScoreToDatabase(uid: String, score: Int, username: String) {
        // Create an instance of ScoreQuiz
        val scoreQuiz = ScoreQuiz(uid, score, username)

        // Get a reference to the scores in the database
        val scoresReference = FirebaseDatabase.getInstance().getReference("scores")

        // Generate a unique id for the attempt
        val id_attempt = UUID.randomUUID().toString()

        // Save the ScoreQuiz instance to the database with the new key
        scoresReference.child(id_attempt).setValue(scoreQuiz)
    }
}