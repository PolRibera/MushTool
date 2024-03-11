package com.example.Projecte3MushTool
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ResponderComentarioActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var postId: String
    private lateinit var commentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder_comentario)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        postId = intent.getStringExtra("postId") ?: ""
        commentId = intent.getStringExtra("commentId") ?: ""

        val editTextRespuesta = findViewById<EditText>(R.id.editTextRespuesta)
        val buttonEnviarRespuesta = findViewById<Button>(R.id.buttonEnviarRespuesta)

        buttonEnviarRespuesta.setOnClickListener {
            val respuesta = editTextRespuesta.text.toString().trim()
            if (respuesta.isNotEmpty()) {
                responderComentario(respuesta)
                finish()
            }
        }
    }

    private fun responderComentario(respuesta: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val respuestaRef = database.child("ForoPost").child(postId).child("comments").child(commentId).child("respuestas").push()
            val respuestaData = HashMap<String, Any>()
            respuestaData["text"] = respuesta
            respuestaData["userId"] = userId
            respuestaRef.setValue(respuestaData)
                .addOnSuccessListener {
                    // La respuesta se guardó exitosamente
                    Log.d("ResponderComentario", "Respuesta guardada en Firebase")
                }
                .addOnFailureListener { e ->
                    // Error al guardar la respuesta
                    Log.e("ResponderComentario", "Error al guardar la respuesta: ${e.message}")
                }
        }
    }

}
