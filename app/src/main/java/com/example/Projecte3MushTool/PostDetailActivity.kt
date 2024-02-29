package com.example.Projecte3MushTool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostDetailActivity  : AppCompatActivity(){

    private lateinit var postReference: DatabaseReference
    private lateinit var userReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedPost: Post
    private lateinit var auhtorUsername: String

            // Retrieve the intent and its extras her
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = getIntent()
        selectedPost = intent.getSerializableExtra("selectedPost") as Post

        auth = FirebaseAuth.getInstance()
        postReference = FirebaseDatabase.getInstance().getReference("Post")
        userReference = FirebaseDatabase.getInstance().getReference("Usuari")
                userReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userBD = dataSnapshot.child(selectedPost.uid)
                        auhtorUsername = userBD.child("username").getValue(String::class.java).toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle cancellation
                    }
                })
        super.onCreate(savedInstanceState)
        setContent {
            ShowSelectedPost(this)
        }
    }
    private fun startMainActivityWithUid(uid: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("uid", uid)
        }
        startActivity(intent)
        finish()
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowSelectedPost(context: Context) {
        var showDialog by remember { mutableStateOf(false) }
        var selectedUsers by remember { mutableStateOf(listOf<User>()) }
        var allUsers by remember { mutableStateOf(listOf<User>()) } // Fetch all users from Firebase
        var checkboxStates by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
        LaunchedEffect(true) {
            val users = mutableListOf<User>()
            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val uid = userSnapshot.key ?: ""
                        val username = userSnapshot.child("username").getValue(String::class.java).toString()
                        val email = userSnapshot.child("email").getValue(String::class.java).toString()
                        users.add(User(uid, username,email))
                    }
                    allUsers = users
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancellation
                }
            })
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(auhtorUsername, color = Color(0xFF6B0C0C))
                    Image(
                        painter = rememberImagePainter(selectedPost.imgPath),
                        contentDescription = "Image of the post",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Text(text = "Comentario: ${selectedPost.comentario}")
                    Text(text = "Tipo de Seta Encontrada: ${selectedPost.setaPost}")
                    Text(text = "Localizacion: ${selectedPost.location}")

                    Button(onClick = {
                        checkboxStates = allUsers.associate { user -> user.uid to (selectedPost.userShare?.split(";")?.contains(user.uid) ?: false) }
                        showDialog = true
                    }) {
                        Text("Share Post")
                    }

                }
            }
        }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Select Users") },
                    text = {
                        LazyColumn {
                            items(allUsers) { user ->
                                val isChecked = selectedPost.userShare?.split(";")?.contains(user.uid) ?: false
                                Checkbox(
                                    checked = checkboxStates[user.uid] ?: false,
                                    onCheckedChange = { isChecked ->
                                        checkboxStates = checkboxStates + (user.uid to isChecked)
                                        if (isChecked) {
                                            if (!selectedUsers.contains(user)) {
                                                selectedUsers = selectedUsers + user
                                            }
                                        } else {
                                            if (selectedUsers.contains(user)) {
                                                selectedUsers = selectedUsers - user
                                            }
                                        }
                                    }
                                )
                                Text(user.username)
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            selectedPost.userShare = selectedUsers.joinToString(";") { it.uid }
                            // Update the post in Firebase // Replace this with the actual key of the post
                            postReference.child(selectedPost.key).setValue(selectedPost)
                            showDialog = false
                        }) {
                            Text("Send")
                        }
                    }
                )
            }

            // ... existing code ...
        }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ShowSelectedPost(this)
    }
}
