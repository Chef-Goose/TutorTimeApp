package com.example.tutorapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import android.widget.ImageButton
import android.content.Intent
import android.widget.Button


class TutorProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_profile)

        // Retrieve the tutorId (which is the full name) from the Intent
        val tutorId = intent.getStringExtra("tutorId")
        val studentId = intent.getStringExtra("studentId")

        if (tutorId != null && tutorId.isNotEmpty()) {
            fetchTutorProfile(tutorId)
        } else {
            Toast.makeText(this, "Invalid tutor ID", Toast.LENGTH_SHORT).show()
        }
        // Handle the Back Button Click
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()  // This will call the system's back behavior
        }
        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            // Navigate to the main activity (home screen)
            val intent = Intent(this, DashboardNavBar::class.java)  // Replace MainActivity with your home activity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear previous activities
            startActivity(intent)
        }
        val feedbackButton: Button = findViewById(R.id.feedback_button)
        feedbackButton.setOnClickListener {
            // Code to handle the button click
            val intent = Intent(this@TutorProfile, StudentFeedback::class.java)
            intent.putExtra("tutorId",tutorId)
            intent.putExtra("studentId",studentId)
            startActivity(intent)
            Toast.makeText(this, "Feedback Button Clicked!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun fetchTutorProfile(tutorId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        // Query Firebase for a tutor where fullName matches tutorId
        val query = usersRef.orderByChild("fullName").equalTo(tutorId)

        query.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Assuming there's only one user with that full name
                val tutor = snapshot.children.firstOrNull()?.getValue(Users::class.java)
                tutor?.let {
                    // Display the tutor's data in the TextViews
                    val fullNameTextView = findViewById<TextView>(R.id.fullNameTextView)
                    val emailTextView = findViewById<TextView>(R.id.emailTextView)
                    val roleTextView = findViewById<TextView>(R.id.roleTextView)
                    val onboardingTextView = findViewById<TextView>(R.id.onboardingTextView)

                    fullNameTextView.text = it.fullName
                    emailTextView.text = it.email
                    roleTextView.text = it.role
                    onboardingTextView.text = if (it.onboarding) "Onboarding Completed" else "Onboarding Pending"
                }
            } else {
                Toast.makeText(this@TutorProfile, "Tutor not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this@TutorProfile, "Error fetching tutor data: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
