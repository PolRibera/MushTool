package com.example.Projecte3MushTool

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CrearPostActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var editTextPost: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_post)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("ForoPost")

        editTextPost = findViewById(R.id.editTextNuevoPost) // Corregir el ID del EditText

        val buttonGuardar = findViewById<Button>(R.id.buttonPublicar) // Corregir el ID del botón
        buttonGuardar.setOnClickListener {
            guardarPost()
        }
    }

    private fun guardarPost() {
        val texto = editTextPost.text.toString().trim()
        if (texto.isNotEmpty()) {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val postId = database.push().key
                if (postId != null) {
                    val post = ForoPost(postId, userId, texto)
                    database.child(postId).setValue(post)
                    finish()
                } else {
                    // Manejar la situación en la que no se pudo obtener un ID de post válido
                    Toast.makeText(this, "Error al guardar el post. Inténtalo de nuevo más tarde.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Manejar la situación en la que el usuario no está autenticado
                Toast.makeText(this, "Debes iniciar sesión para publicar un post.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            // Manejar la situación en la que el usuario no ingresó ningún texto
            Toast.makeText(this, "Debes ingresar un texto para publicar un post.", Toast.LENGTH_SHORT).show()
        }
    }

}
