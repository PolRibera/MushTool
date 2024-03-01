package com.example.Projecte3MushTool

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class EditForoPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var postId: String
    private lateinit var editTextPost: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_foro_post)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference.child("posts")

        editTextPost = findViewById(R.id.editTextPost)

        postId = intent.getStringExtra("postId") ?: ""

        // Obtener el texto actual del post desde Firebase y mostrarlo en el EditText
        database.child(postId).get().addOnSuccessListener { snapshot ->
            val post = snapshot.getValue(ForoPost::class.java)
            editTextPost.setText(post?.text)
        }.addOnFailureListener { e ->
            Log.e("EditForoPostActivity", "Error al obtener el post: $e")
        }

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            // Guardar el texto editado del post en Firebase
            val newText = editTextPost.text.toString().trim()
            if (newText.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    database.child(postId).child("text").setValue(newText)
                        .addOnSuccessListener {
                            Toast.makeText(this@EditForoPostActivity, "Post editado exitosamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("EditForoPostActivity", "Error al editar el post: $e")
                        }
                } else {
                    Toast.makeText(this@EditForoPostActivity, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this@EditForoPostActivity, "Debes ingresar un texto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
