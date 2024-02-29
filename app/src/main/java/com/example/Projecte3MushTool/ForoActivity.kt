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
            startActivity(Intent(this, CrearPostActivity::class.java))
        }

        // Cambios en ValueEventListener para mostrar todos los posts
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

        val textViewUserName = TextView(this@ForoActivity)
        // Agregar nombre de usuario
        textViewUserName.text = foroPost.userId

        val textViewPost = TextView(this@ForoActivity)
        // Agregar el texto del post
        textViewPost.text = foroPost.text

        // Añadir borde al contenedor
        container.background = ContextCompat.getDrawable(this@ForoActivity, R.drawable.post_border)

        // Agregar OnClickListener al contenedor para abrir ViewPostActivity
        container.setOnClickListener {
            val intent = Intent(this@ForoActivity, ViewPostActivity::class.java)
            intent.putExtra("postId", foroPost.id)
            startActivity(intent)
        }

        // Añadir botones de editar y borrar solo si el usuario actual es el creador del post
        if (auth.currentUser?.uid == foroPost.userId) {
            val editButton = Button(this@ForoActivity)
            editButton.text = "Editar"
            // Agregar OnClickListener para editar
            editButton.setOnClickListener {
                val intent = Intent(this@ForoActivity, EditForoPostActivity::class.java)
                intent.putExtra("postId", foroPost.id)
                startActivity(intent)
            }

            val deleteButton = Button(this@ForoActivity)
            deleteButton.text = "Borrar"
            // Agregar OnClickListener para borrar
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

        container.addView(textViewUserName)
        container.addView(textViewPost)

        layoutPosts.addView(container)
    }
}
