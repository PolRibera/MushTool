package com.example.Projecte3MushTool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var postId: String
    private lateinit var commentsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        postId = intent.getStringExtra("postId") ?: ""

        val textViewPost = findViewById<TextView>(R.id.textViewPost)
        val editTextComment = findViewById<EditText>(R.id.editTextComment)
        val buttonAddComment = findViewById<Button>(R.id.buttonAddComment)
        commentsLayout = findViewById(R.id.commentsLayout)

        loadPostDetails()

        buttonAddComment.setOnClickListener {
            val commentText = editTextComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
                editTextComment.text.clear()
            }
        }
    }

    private fun loadPostDetails() {
        val postRef = database.child("ForoPost").child(postId)
        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(ForoPost::class.java)
                if (post != null) {
                    val userId = post.userId
                    val displayUserId = userId
                    findViewById<TextView>(R.id.textViewPost).text = "$displayUserId\n${post.text}"
                    loadComments(postId) // Cargar los comentarios asociados a este post
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewPostActivity", "Error al cargar la publicación: ${error.message}")
            }
        })
    }

    private fun loadComments(postId: String) {
        val commentsRef = database.child("ForoPost").child(postId).child("comments")
        commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentsLayout.removeAllViews() // Limpiar el diseño de comentarios antes de agregar nuevos comentarios
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment != null) {
                        val userId = comment.userId
                        val commentText = comment.text

                        // Crear una vista para mostrar el comentario y el botón de respuesta
                        val commentContainer = LinearLayout(this@ViewPostActivity)
                        commentContainer.orientation = LinearLayout.VERTICAL

                        val commentTextView = TextView(this@ViewPostActivity)
                        if (userId == auth.currentUser?.uid) {
                            // Si el comentario fue realizado por el usuario actual, añadir un prefijo para distinguirlo
                            commentTextView.text = "Tú: $commentText"
                        } else {
                            commentTextView.text = "$userId:$commentText"
                        }

                        // Mostrar las respuestas al comentario
                        val respuestasRef = commentSnapshot.child("respuestas")
                        for (respuestaSnapshot in respuestasRef.children) {
                            // Acceder a cada campo de la respuesta individualmente
                            val userId = respuestaSnapshot.child("userId").getValue(String::class.java)
                            val respuestaText = respuestaSnapshot.child("text").getValue(String::class.java)

                            // Verificar si los campos son no nulos o vacíos antes de mostrar la respuesta
                            if (!userId.isNullOrEmpty() && !respuestaText.isNullOrEmpty()) {
                                // Construir una cadena que represente la respuesta
                                val respuestaString = "$userId:$respuestaText"

                                // Crear un TextView para mostrar la respuesta
                                val respuestaTextView = TextView(this@ViewPostActivity)
                                respuestaTextView.text = respuestaString
                                commentContainer.addView(respuestaTextView)
                            }
                        }


                        val replyButton = Button(this@ViewPostActivity)
                        replyButton.text = "Responder"
                        replyButton.setOnClickListener {
                            // Iniciar la actividad de respuesta al comentario
                            val intent = Intent(this@ViewPostActivity, ResponderComentarioActivity::class.java)
                            intent.putExtra("postId", postId)
                            intent.putExtra("commentId", comment.id)
                            startActivity(intent)
                        }

                        commentContainer.addView(commentTextView)
                        commentContainer.addView(replyButton)

                        commentsLayout.addView(commentContainer)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewPostActivity", "Error al cargar los comentarios: ${error.message}")
            }
        })
    }



    private fun addComment(commentText: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val commentId = database.child("ForoPost").child(postId).child("comments").push().key
            val comment = Comment(commentId, userId, commentText)
database.child("ForoPost").child(postId).child("comments").child(commentId!!).setValue(comment)
                .addOnSuccessListener {
                    // El comentario se guardó exitosamente
                    Log.d("ViewPostActivity", "Comentario guardado en Firebase")
                }
                .addOnFailureListener { e ->
                    // Error al guardar el comentario
                    Log.e("ViewPostActivity", "Error al guardar el comentario: ${e.message}")
                }
            // Actualizar la vista para mostrar el nuevo comentario
            val commentTextView = TextView(this@ViewPostActivity)
            commentTextView.text = "$userId: $commentText"
            commentsLayout.addView(commentTextView)
        }
    }
}
