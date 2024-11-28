package com.example.tutorapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ViewFeedbackActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var feedbackTextView: TextView
    private lateinit var feedbackInput: EditText
    private lateinit var backButton: ImageButton
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_feedback)

        // Initialize views
        feedbackTextView = findViewById(R.id.feedbackTextView)
        feedbackInput = findViewById(R.id.feedbackInput)
        backButton = findViewById(R.id.back_button)
        submitButton = findViewById(R.id.submitButton)

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Navigate back to the previous screen
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Feedback")

        // Load existing feedback
        loadFeedback()

        // Submit feedback functionality
        submitButton.setOnClickListener {
            submitFeedback()
        }
    }

    /**
     * Fetch and display feedback from Firebase.
     */
    private fun loadFeedback() {
        feedbackTextView.text = "Loading feedback, please wait..."

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedbackList = StringBuilder()
                if (snapshot.exists()) {
                    for (feedback in snapshot.children) {
                        val feedbackText = feedback.getValue(String::class.java)
                        if (!feedbackText.isNullOrEmpty()) {
                            feedbackList.append("- $feedbackText\n\n")
                        }
                    }
                } else {
                    feedbackList.append("No feedback available at the moment.")
                }
                feedbackTextView.text = feedbackList.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                feedbackTextView.text = "Failed to load feedback."
                Toast.makeText(
                    this@ViewFeedbackActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * Submit user feedback to Firebase.
     */
    private fun submitFeedback() {
        val feedbackText = feedbackInput.text.toString().trim()
        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback.", Toast.LENGTH_SHORT).show()
            return
        }

        // Push feedback to the database
        val newFeedbackRef = database.push()
        newFeedbackRef.setValue(feedbackText)
            .addOnSuccessListener {
                Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()
                feedbackInput.text.clear() // Clear the input field
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}
