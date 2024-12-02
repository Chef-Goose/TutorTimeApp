package com.example.tutorapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ViewFeedbackActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_feedback)

        val feedbackTextView = findViewById<TextView>(R.id.feedbackTextView)
        val backButton = findViewById<ImageButton>(R.id.back_button)

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Navigate back to the previous screen
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Feedback") // Update with your feedback data node

        // Fetch feedback from the database
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedbackList = StringBuilder("Feedback:\n\n")
                for (feedback in snapshot.children) {
                    val feedbackText = feedback.getValue(String::class.java)
                    if (!feedbackText.isNullOrEmpty()) {
                        feedbackList.append("- $feedbackText\n\n")
                    }
                }
                feedbackTextView.text = feedbackList.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                feedbackTextView.text = "Failed to load feedback: ${error.message}"
            }
        })
    }
}
