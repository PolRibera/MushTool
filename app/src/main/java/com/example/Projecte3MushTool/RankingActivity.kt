package com.example.Projecte3MushTool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

class RankingActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var scoresReference: DatabaseReference
    private var scores by mutableStateOf<List<ScoreQuiz>>(emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        scoresReference = FirebaseDatabase.getInstance().getReference("scores")

        setContent {
            AppTheme {
                RankingApp(context = this)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun RankingApp(context: Context) {
        Scaffold(
            topBar = {
                TopAppBar(
                    {
                        Text(text = "Ranking")

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                filterScoresByCurrentDate()
                            }) {
                                Text(text = "Top of the Day")
                            }

                            Button(onClick = {
                                filterScoresByCurrentWeek()
                            }) {
                                Text(text = "Top of the Week")
                            }

                        }
                    }

                )


                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(onClick = {
                        filterScoresByCurrentUser()
                    }) {
                        Text(text = "My Scores")
                    }

                    Button(onClick = {
                        showAllScores()
                    }) {
                        Text(text = "All scores")
                    }
                }
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
                                Text (text = "Fecha")
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
                                    Text(text =scoreQuiz.date)
                                }
                            }
                        }
                    }
                }
            }

        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterScoresByCurrentWeek() {
        val scoresReference = FirebaseDatabase.getInstance().getReference("scores")
        scoresReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scoreList = mutableListOf<ScoreQuiz>()
                val currentDateTime = LocalDateTime.now()
                val currentWeek = currentDateTime.get(ChronoField.ALIGNED_WEEK_OF_YEAR)

                for (scoreSnapshot in snapshot.children) {
                    val score = scoreSnapshot.getValue(ScoreQuiz::class.java)
                    score?.let {
                        val scoreDateTime = LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val scoreWeek = scoreDateTime.get(ChronoField.ALIGNED_WEEK_OF_YEAR)
                        if (scoreWeek == currentWeek) {
                            scoreList.add(it)
                        }
                    }
                }
                scores = scoreList.sortedByDescending { it.score }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun showAllScores() {
        loadScores()
    }

    private fun filterScoresByCurrentUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            scoresReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val scoreList = mutableListOf<ScoreQuiz>()
                    for (scoreSnapshot in snapshot.children) {
                        val score = scoreSnapshot.getValue(ScoreQuiz::class.java)
                        score?.let {
                            if (it.uid == uid) {
                                scoreList.add(it)
                            }
                        }
                    }
                    scores = scoreList.sortedByDescending { it.score }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterScoresByCurrentDate() {
        val scoresReference = FirebaseDatabase.getInstance().getReference("scores")
        scoresReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scoreList = mutableListOf<ScoreQuiz>()
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val currentDate = currentDateTime.format(formatter)

                for (scoreSnapshot in snapshot.children) {
                    val score = scoreSnapshot.getValue(ScoreQuiz::class.java)
                    score?.let {
                        if (it.date.startsWith(currentDate)) {
                            scoreList.add(it)
                        }
                    }
                }
                scores = scoreList.sortedByDescending { it.score }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}


