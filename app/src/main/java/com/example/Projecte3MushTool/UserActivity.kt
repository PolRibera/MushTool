package com.example.Projecte3MushTool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lemonade.ui.theme.AppTheme
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage

class UserActivity : ComponentActivity() {
    private lateinit var userReference: DatabaseReference
    private lateinit var storageReference: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        storageReference = FirebaseStorage.getInstance()
        userReference = FirebaseDatabase.getInstance().getReference("Users")

        setContent {
            AppTheme {
                BusquedaApp(this)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BusquedaApp(context: Context) {
        var usersState by remember { mutableStateOf<List<User>>(emptyList()) }
        var selectedUser by remember { mutableStateOf<User?>(null) }
        var showDialog by remember { mutableStateOf(false) }

        LaunchedEffect(true) {
            val users = mutableListOf<User>()

            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val newUsers = mutableListOf<User>()

                    for (userSnapshot in dataSnapshot.children) {
                        val userId = userSnapshot.key ?: ""
                        val name = userSnapshot.child("name").getValue(String::class.java) ?: ""
                        val email = userSnapshot.child("email").getValue(String::class.java) ?: ""
                        val password = userSnapshot.child("password").getValue(String::class.java) ?: ""

                        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                            val user = User(userId, name, email, password)
                            newUsers.add(user)
                        }
                    }
                    usersState = newUsers
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }

        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
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
                                Text("Exit")
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(context, CrearUsuarioActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .background(Color(0xFF6B0C0C))
                    ) {
                        Text("Add User")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn {
                        items(usersState) { user ->
                            Row() {
                                Column() {
                                    Text(text = "ID: ${user.userId}")
                                    Text(text = "Name: ${user.name}")
                                    Text(text = "Email: ${user.email}")
                                    Text(text = "Password: ${user.password}")
                                    Row {
                                        Button(onClick = {
                                            selectedUser = user
                                            showDialog = true
                                        }) {
                                            Text("Delete")
                                        }
                                        Button(onClick = {
                                            val intent =
                                                Intent(context, EditarUsuarioActivity::class.java)
                                            intent.putExtra("userId", user.userId)
                                            intent.putExtra("name", user.name)
                                            intent.putExtra("email", user.email)
                                            intent.putExtra("password", user.password)
                                            context.startActivity(intent)
                                        }) {
                                            Text("Edit")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (showDialog) {
                                        AlertDialog(
                                            onDismissRequest = {
                                                showDialog = false
                                                selectedUser = null
                                            },
                                            title = {
                                                Text("Confirmation")
                                            },
                                            text = {
                                                Text("Are you sure you want to delete this user?")
                                            },
                                            confirmButton = {
                                                Button(
                                                    onClick = {
                                                        showDialog = false
                                                        selectedUser?.let { user ->
                                                            // Remove user from database
                                                            userReference.child(user.userId)
                                                                .removeValue()
                                                            Toast.makeText(
                                                                context,
                                                                "User deleted successfully",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            selectedUser = null
                                                        }
                                                    }
                                                ) {
                                                    Text("Confirm")
                                                }
                                            },
                                            dismissButton = {
                                                Button(
                                                    onClick = {
                                                        showDialog = false
                                                        selectedUser = null
                                                    }
                                                ) {
                                                    Text("Cancel")
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                        }

                    }

                }

            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppTheme {
            BusquedaApp(LocalContext.current)
        }
    }
}
