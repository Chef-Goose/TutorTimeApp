package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class TutorProfileBio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_profile)

        // Hide the action bar
        supportActionBar?.hide()

        // Initialize UserPreferences
        UserPreferences.init(this)

        // Back button functionality
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Fetch the logged-in user ID from SharedPreferences
        val loggedInUserId = UserPreferences.getLoggedInUserId()

        Log.d("UserProfile", "Logged-in user ID: $loggedInUserId")

        if (loggedInUserId != null) {
            // Fetch user profile data from Firebase using the logged-in user ID
            fetchUserProfile(loggedInUserId)
        } else {
            Toast.makeText(this, "No logged-in user found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserProfile(userId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        // Query Firebase for the user based on the user ID
        val query = usersRef.orderByChild("id").equalTo(userId)

        query.get().addOnSuccessListener { snapshot ->
            Log.d("Firebase", "Snapshot: ${snapshot.value}")
            if (snapshot.exists()) {
                // Assuming the user exists, get the first user from the result
                val user = snapshot.children.firstOrNull()?.getValue(Users::class.java)
                user?.let {
                    // Populate the UI with the user data
                    val fullNameTextView = findViewById<TextView>(R.id.fullNameTextView)
                    val roleTextView = findViewById<TextView>(R.id.roleTextView)
                    val emailTextView = findViewById<TextView>(R.id.emailTextView)
                    val onboardingTextView = findViewById<TextView>(R.id.onboardingTextView)

                    // Set the data to the TextViews
                    fullNameTextView.text = "Name: ${it.fullName}"
                    roleTextView.text = "Role: ${it.role}"
                    emailTextView.text = "Email: ${it.email}"
                    onboardingTextView.text = "Onboarding: ${it.onboarding}"
                }
            } else {
                Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching user data: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
