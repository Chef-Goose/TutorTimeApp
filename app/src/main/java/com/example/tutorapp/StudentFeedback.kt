package com.example.tutorapp

import android.os.Bundle
import android.widget.Toast
import android.widget.ImageButton
import android.widget.Button
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity

// Define a data model for the feedback without the timestamp
data class Feedback(val feedbackText: String)

class StudentFeedback : AppCompatActivity() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val feedbackRef: DatabaseReference = database.reference.child("feedbacks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_givefeedback)

        val tutorId = intent.getStringExtra("tutorId")
        val studentId = intent.getStringExtra("studentId")
        // Handle the Back Button Click
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()  // This will call the system's back behavior
        }

        val feedbackEditText: TextInputEditText = findViewById(R.id.tb_feedback)

        // Feedback button click listener
        val feedbackButton: Button = findViewById(R.id.feedback_button)
        feedbackButton.setOnClickListener {
            val feedbackText = "Tutor ID: " + tutorId + " Student ID: " + studentId + " Feedback: " + feedbackEditText.text.toString()

            // Check if the feedback is not empty
            if (feedbackText.isNotEmpty()) {
                // Create a Feedback object with the current feedback text
                val feedback = Feedback(feedbackText)

                // Push the feedback to Firebase Database
                feedbackRef.push().setValue(feedback)
                    .addOnSuccessListener {
                        // Show success message
                        Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()
                        feedbackEditText.text?.clear() // Clear the input field
                    }
                    .addOnFailureListener { e ->
                        // Show error message if something goes wrong
                        Toast.makeText(this, "Failed to submit feedback: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Notify user if feedback is empty
                Toast.makeText(this, "Please enter some feedback!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
