package com.example.Projecte3MushTool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ForoActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var layoutPosts: LinearLayout
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foro)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("ForoPost")

        userId = auth.currentUser?.uid ?: ""

        val buttonCrearPost = findViewById<Button>(R.id.buttonCrearPost)
        layoutPosts = findViewById(R.id.layoutPosts)

        buttonCrearPost.setOnClickListener {
            startActivity(Intent(this, CrearForoPostActivity::class.java))
        }

        // ValueEventListener to fetch posts and usernames
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                layoutPosts.removeAllViews()
                for (postSnapshot in snapshot.children) {
                    val foroPost = postSnapshot.getValue(ForoPost::class.java)
                    if (foroPost != null) {
                        addPostToLayout(foroPost)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ForoActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun addPostToLayout(foroPost: ForoPost) {
        val container = LinearLayout(this@ForoActivity)
        container.orientation = LinearLayout.VERTICAL

        val textViewUserName = TextView(this@ForoActivity)
        getUserDisplayName(foroPost.userId, textViewUserName)

        val textViewPost = TextView(this@ForoActivity)
        textViewPost.text = foroPost.text

        container.background = ContextCompat.getDrawable(this@ForoActivity, R.drawable.post_border)

        container.setOnClickListener {
            val intent = Intent(this@ForoActivity, ViewPostActivity::class.java)
            intent.putExtra("postId", foroPost.id)
            startActivity(intent)
        }

        if (auth.currentUser?.uid == foroPost.userId) {
            val editButton = Button(this@ForoActivity)
            editButton.text = "Edit"
            editButton.setOnClickListener {
                val intent = Intent(this@ForoActivity, EditForoPostActivity::class.java)
                intent.putExtra("postId", foroPost.id)
                startActivity(intent)
            }

            val deleteButton = Button(this@ForoActivity)
            deleteButton.text = "Delete"
            deleteButton.setOnClickListener {
                val postId = foroPost.id
                if (postId != null) {
                    database.child(postId).removeValue()
                }
                Toast.makeText(this@ForoActivity, "Deleted successfully", Toast.LENGTH_SHORT).show()
            }

            container.addView(editButton)
            container.addView(deleteButton)
        }

        container.addView(textViewUserName)
        container.addView(textViewPost)

        layoutPosts.addView(container)
    }

    private fun getUserDisplayName(userId: String, textViewUserName: TextView) {
        val userReference = FirebaseDatabase.getInstance().reference.child("Usuari")
        userReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val username = user?.username ?: "Unknown"
                textViewUserName.text = "Username: $username"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ForoActivity", "Error getting username: ${error.message}")
            }
        })
    }
}

