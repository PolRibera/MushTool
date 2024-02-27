package com.example.Projecte3MushTool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import coil.compose.rememberImagePainter

class ListarPostsActivity : ComponentActivity() {
    private lateinit var postReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postReference = FirebaseDatabase.getInstance().getReference("Post")

        setContent {
            ListarPostsApp(this)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ListarPostsApp(context: Context) {
        var postsState by remember { mutableStateOf<List<Post>>(emptyList()) }
        var selectedPost by remember { mutableStateOf<Post?>(null) }
        var showDialog by remember { mutableStateOf(false) }

        LaunchedEffect(true) {
            val posts = mutableListOf<Post>()
            postReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val newPosts = mutableListOf<Post>()

                    for (postSnapshot in dataSnapshot.children) {
                        val comentario =
                            postSnapshot.child("comentario").getValue(String::class.java)
                        val imgPath = postSnapshot.child("imgPath").getValue(String::class.java)
                        val sciNameSeta =
                            postSnapshot.child("setaPost").getValue(String::class.java)
                        val locationString =
                            postSnapshot.child("location").getValue(String::class.java)

                        if (imgPath != null && comentario != null && sciNameSeta != null && locationString != null) {
                            val post = Post(imgPath, comentario, sciNameSeta, locationString)
                            newPosts.add(post)
                        }
                    }
                    postsState = newPosts
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancellation
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
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Button(
                        onClick = {
                            val intent = Intent(context, PostActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Añadir Post")
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        items(postsState) { post ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        selectedPost = post
                                        showDialog = true
                                    }
                            ) {
                                Image(
                                    painter = rememberImagePainter(post.imgPath),
                                    contentDescription = "Image of the post",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                                Text(text = "Comentario: ${post.comentario}")
                            }
                        }
                    }

                    if (showDialog && selectedPost != null) {
                        AlertDialog(
                            onDismissRequest = {
                                showDialog = false
                                selectedPost = null
                            },
                            title = {
                                Text("Detalles del Post")
                            },
                            text = {
                                Column {
                                    Text(text = "Comentario: ${selectedPost!!.comentario}")
                                    Text(text = "Nombre Científico de la Seta: ${selectedPost!!.setaPost}")
                                    Text(text = "Ubicación: ${selectedPost!!.location}")
                                }
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
                                    onClick = {
                                        showDialog = false
                                    }
                                ) {
                                    Text("Cerrar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ListarPostsApp(this)
    }
}
