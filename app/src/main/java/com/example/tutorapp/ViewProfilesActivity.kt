package com.example.tutorapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ViewProfilesActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profiles)

        val profilesTextView = findViewById<TextView>(R.id.profilesTextView)
        val backButton = findViewById<ImageButton>(R.id.back_button)

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Navigate back to the previous screen
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Fetch profiles from the database
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profiles = StringBuilder("Profiles:\n\n")
                for (user in snapshot.children) {
                    val fullName = user.child("fullName").getValue(String::class.java) ?: "N/A"
                    val role = user.child("role").getValue(String::class.java) ?: "N/A"
                    val email = user.child("email").getValue(String::class.java) ?: "N/A"
                    profiles.append("Name: $fullName\nRole: $role\nEmail: $email\n\n")
                }
                profilesTextView.text = profiles.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                profilesTextView.text = "Failed to load profiles: ${error.message}"
            }
        })
    }
}
