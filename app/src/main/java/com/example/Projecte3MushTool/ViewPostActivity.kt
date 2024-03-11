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
    private lateinit var userReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        userReference = FirebaseDatabase.getInstance().reference.child("Usuari")

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
                    getUserUsername(userId) { username ->
                        val displayUserName = username ?: userId
                        findViewById<TextView>(R.id.textViewPost).text = "$displayUserName\n${post.text}"
                        loadComments(postId) // Load comments associated with this post
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewPostActivity", "Error loading post: ${error.message}")
            }
        })
    }


    private fun loadComments(postId: String) {
        val commentsRef = database.child("ForoPost").child(postId).child("comments")
        commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentsLayout.removeAllViews() // Clear comments layout before adding new comments
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment != null) {
                        val userId = comment.userId
                        getUserUsername(userId) { username ->
                            val commentText = comment.text
                            val displayUserName = if (userId == auth.currentUser?.uid) {
                                "Tú"
                            } else {
                                username ?: userId
                            }

                            val commentContainer = LinearLayout(this@ViewPostActivity)
                            val commentTextView = TextView(this@ViewPostActivity)
                            commentTextView.text = "$displayUserName: $commentText"
                            commentContainer.addView(commentTextView)

                            val replyButton = Button(this@ViewPostActivity)
                            replyButton.text = "Responder"
                            replyButton.setOnClickListener {
                                // Start the activity to reply to the comment
                                val intent = Intent(this@ViewPostActivity, ResponderComentarioActivity::class.java)
                                intent.putExtra("postId", postId)
                                intent.putExtra("commentId", comment.id)
                                startActivity(intent)
                            }
                            val repliesRef = commentSnapshot.child("respuestas")


                            commentContainer.addView(replyButton) // Add the reply button to the container
                            commentsLayout.addView(commentContainer)
                            for (replySnapshot in repliesRef.children) {
                                // Extract reply details
                                val replyUserId = replySnapshot.child("userId").getValue(String::class.java)
                                val replyText = replySnapshot.child("text").getValue(String::class.java)


                                // Display the reply in the UI
                                val replyTextView = TextView(this@ViewPostActivity)
                                val replyUser = if (replyUserId == auth.currentUser?.uid) {
                                    "Tú"
                                } else {
                                    username ?: replyUserId
                                }

                                replyTextView.text = "$replyUser: $replyText"
                                commentsLayout.addView(replyTextView)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewPostActivity", "Error loading comments: ${error.message}")
            }
        })
    }

    private fun getUserUsername(userId: String, callback: (String?) -> Unit) {
        userReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                callback(user?.username)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewPostActivity", "Error loading user: ${error.message}")
                callback(null)
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
                    // Comment saved successfully
                    Log.d("ViewPostActivity", "Comment saved to Firebase")
                }
                .addOnFailureListener { e ->
                    // Error saving comment
                    Log.e("ViewPostActivity", "Error saving comment: ${e.message}")
                }
            // Update the view to display the new comment
            getUserUsername(userId) { username ->
                val displayUserName = username ?: userId
                val commentTextView = TextView(this@ViewPostActivity)
                commentTextView.text = "$displayUserName: $commentText"
                commentsLayout.addView(commentTextView)
            }
        }
    }
}