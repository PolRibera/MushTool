package com.example.Projecte3MushTool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ForoActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var layoutPosts: LinearLayout // Declaración de la propiedad layoutPosts
    private var creatorId: String = "" // Variable para almacenar el ID del creador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foro)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("posts")

        val editTextNewPost = findViewById<EditText>(R.id.editTextNewPost)
        val buttonPost = findViewById<Button>(R.id.buttonPost)
        layoutPosts = findViewById(R.id.layoutPosts) // Inicialización de layoutPosts

        buttonPost.setOnClickListener {
            val newPostText = editTextNewPost.text.toString().trim()
            if (newPostText.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    creatorId = userId // Almacenar el ID del creador
                    val postId = database.push().key
                    val post = ForoPost(postId, userId, newPostText)
                    database.child(postId!!).setValue(post)
                    editTextNewPost.text.clear()
                } else {
                    Toast.makeText(this@ForoActivity, "Debes iniciar sesión para publicar un post", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ForoActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this@ForoActivity, "Debes ingresar un texto para publicar un post", Toast.LENGTH_SHORT).show()
            }
        }

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
                Log.e("ForoActivity", "Error en la base de datos: ${error.message}")
            }
        })
    }

    private fun addPostToLayout(foroPost: ForoPost) {
        val container = LinearLayout(this@ForoActivity)
        container.orientation = LinearLayout.VERTICAL

        val textViewPost = TextView(this@ForoActivity)
        textViewPost.text = foroPost.text

        if (auth.currentUser?.uid == creatorId) { // Verificar si el usuario actual es el creador del post
            val editButton = Button(this@ForoActivity)
            editButton.text = "Editar"
            editButton.setOnClickListener {
                val intent = Intent(this@ForoActivity, EditForoPostActivity::class.java)
                intent.putExtra("postId", foroPost.id)
                startActivity(intent)
            }

            val deleteButton = Button(this@ForoActivity)
            deleteButton.text = "Borrar"
            deleteButton.setOnClickListener {
                val postId = foroPost.id
                if (postId != null) {
                    database.child(postId).removeValue()
                }
                Toast.makeText(this@ForoActivity, "Borrado exitosamente", Toast.LENGTH_SHORT).show()
            }

            container.addView(editButton)
            container.addView(deleteButton)
        }

        container.addView(textViewPost)

        layoutPosts.addView(container)
    }
}

