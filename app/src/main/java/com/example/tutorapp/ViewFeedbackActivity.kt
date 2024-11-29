package com.example.tutorapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ViewFeedbackActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var feedbackTextView: TextView
    private lateinit var feedbackInput: EditText
    private lateinit var backButton: ImageButton
    private lateinit var submitButton: Button
    private lateinit var feedbackListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_feedback)

        // Correct initialization of views
        feedbackTextView = findViewById(R.id.feedTextView)
        feedbackInput = findViewById(R.id.feedbackInput)
        backButton = findViewById(R.id.back_button)
        submitButton = findViewById(R.id.submitButton)

        // Back button functionality
        backButton.setOnClickListener {
            finish()
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Feedback")

        // Load existing feedback
        feedbackListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedbackList = StringBuilder()
                if (snapshot.exists()) {
                    for (feedback in snapshot.children) {
                        val feedbackText = feedback.getValue(String::class.java)
                        if (!feedbackText.isNullOrEmpty()) {
                            feedbackList.append("- $feedbackText\n\n")
                        } else {
                            feedbackList.append("- (Invalid feedback entry)\n\n")
                        }
                    }
                } else {
                    feedbackList.append("No feedback available at the moment.")
                }
                feedbackTextView.text = feedbackList.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                feedbackTextView.text = "Failed to load feedback."
                Toast.makeText(this@ViewFeedbackActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
        database.addValueEventListener(feedbackListener)

        // Add TextWatcher to enable/disable submit button dynamically
        feedbackInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                submitButton.isEnabled = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Submit feedback functionality
        submitButton.setOnClickListener { submitFeedback() }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the listener to prevent memory leaks
        database.removeEventListener(feedbackListener)
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
