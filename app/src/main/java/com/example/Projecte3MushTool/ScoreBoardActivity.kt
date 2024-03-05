package com.example.Projecte3MushTool

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ScoreBoardActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userReference: DatabaseReference
    private var username by mutableStateOf("")

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                saveScoreToDatabase(
                    auth.currentUser?.uid ?: "",
                    score,
                    username.value
                )
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveScoreToDatabase(uid: String, score: Int, username: String) {
        // Get the current date and time
        val currentDateTime = LocalDateTime.now()
        // Format the date and time as "year-month-day hour:minute:second"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)

        // Create an instance of ScoreQuiz
        val scoreQuiz = ScoreQuiz(uid, username, score, formattedDateTime)
        // Get a reference to the "scores" node in the database
        val scoresReference = FirebaseDatabase.getInstance().getReference("scores")

        // Generate a unique id for the score
        val scoreId = scoresReference.push().key ?: ""

        // Save the ScoreQuiz instance to the database under the unique scoreId
        scoresReference.child(scoreId).setValue(scoreQuiz)
    }
}