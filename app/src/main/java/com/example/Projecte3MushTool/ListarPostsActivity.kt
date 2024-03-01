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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.google.firebase.auth.FirebaseAuth

class ListarPostsActivity : ComponentActivity() {
    private lateinit var postReference: DatabaseReference
    private lateinit var userReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        postReference = FirebaseDatabase.getInstance().getReference("Post")
        userReference = FirebaseDatabase.getInstance().getReference("Usuari")
        val user = auth.currentUser
        val uid = user?.uid
        var username = ""
        if (uid != null) {
            userReference.addValueEventListener(object :  ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userBD = dataSnapshot.child(uid)
                    username = userBD.child("username").getValue(String::class.java).toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancellation
                }
            })
        }
        setContent {
            ListarPostsApp(this,username)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ListarPostsApp(context: Context, username: String) {
        var postsState by remember { mutableStateOf<List<Post>>(emptyList()) }
        var selectedPost by remember { mutableStateOf<Post?>(null) }
        var showDialog by remember { mutableStateOf(false) }
        val currentUserUid = auth.currentUser?.uid

        var sharedPostsState by remember { mutableStateOf<List<Post>>(emptyList()) }
        LaunchedEffect(true) {
            val posts = mutableListOf<Post>()
            val sharedPosts = mutableListOf<Post>()
            postReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val newPosts = mutableListOf<Post>()
                    val newSharedPosts = mutableListOf<Post>()
                    for (postSnapshot in dataSnapshot.children) {
                        val key = postSnapshot.child("key").getValue(String::class.java)
                        val uid = postSnapshot.child("uid").getValue(String::class.java)
                        val comentario = postSnapshot.child("comentario").getValue(String::class.java)
                        val imgPath = postSnapshot.child("imgPath").getValue(String::class.java)
                        val sciNameSeta = postSnapshot.child("setaPost").getValue(String::class.java)
                        val locationString = postSnapshot.child("location").getValue(String::class.java)
                        val userShare = postSnapshot.child("userShare").getValue(String::class.java)

                        if (uid != null && uid == auth.currentUser?.uid && comentario != null && sciNameSeta != null && locationString != null && imgPath != null && userShare!=null && key!=null) {
                            val post = Post(key,uid, imgPath, comentario, sciNameSeta, locationString, userShare)
                            newPosts.add(post)
                        }
                        if (userShare != null && comentario != null && sciNameSeta != null && locationString != null && imgPath != null && uid!=null && key!=null) {
                            if (userShare.split(";").contains(currentUserUid)) {
                                val post = Post(key,uid, imgPath, comentario, sciNameSeta, locationString, userShare)
                                newSharedPosts.add(post)
                            }
                        }


                    }
                    postsState = newPosts
                    sharedPostsState = newSharedPosts
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancellation
                }
            })
        }

        var selectedOption by remember { mutableStateOf("Mis Posts") }

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

                    Row {
                        RadioButton(
                            selected = selectedOption == "Mis Posts",
                            onClick = { selectedOption = "Mis Posts" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF6B0C0C),
                                unselectedColor = Color.DarkGray
                            )
                        )
                        Text("Mis Posts")

                        RadioButton(
                            selected = selectedOption == "Post Compartidos",
                            onClick = { selectedOption = "Post Compartidos" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF6B0C0C),
                                unselectedColor = Color.DarkGray
                            )
                        )
                        Text("Post Compartidos")
                    }

                    if (selectedOption == "Mis Posts") {
                        Text(text = "Mis Posts")
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            items(postsState) { post ->
                                var authorUsername by remember { mutableStateOf("") }
                                userReference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val userBD = dataSnapshot.child(post.uid)
                                        authorUsername =
                                            userBD.child("username").getValue(String::class.java)
                                                .toString()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle cancellation
                                    }
                                })
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            selectedPost = post
                                            val intent = Intent(context, PostDetailActivity::class.java)
                                            intent.putExtra("selectedPost", selectedPost)
                                            context.startActivity(intent)
                                        }
                                ) {
                                    Text(authorUsername)
                                    Image(
                                        painter = rememberImagePainter(post.imgPath),
                                        contentDescription = "Image of the post",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(text = "Shared Posts")
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            items(sharedPostsState) { post ->
                                var authorUsername by remember { mutableStateOf("") }
                                userReference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val userBD = dataSnapshot.child(post.uid)
                                        authorUsername =
                                            userBD.child("username").getValue(String::class.java)
                                                .toString()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle cancellation
                                    }
                                })
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            selectedPost = post
                                            val intent = Intent(context, PostDetailActivity::class.java)
                                            intent.putExtra("selectedPost", selectedPost)
                                            context.startActivity(intent)
                                        }
                                ) {
                                    Text(authorUsername)
                                    Image(
                                        painter = rememberImagePainter(post.imgPath),
                                        contentDescription = "Image of the post",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
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
        ListarPostsApp(this,"username")
    }
}