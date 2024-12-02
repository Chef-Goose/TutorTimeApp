package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DirectorDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director_dashboard)

        // Example buttons for navigation or actions
        val feedbackButton = findViewById<Button>(R.id.feedbackButton)
        val viewProfilesButton = findViewById<Button>(R.id.viewProfilesButton)
        val statPageButton = findViewById<Button>(R.id.statPageButton)
        val signOutButton = findViewById<Button>(R.id.signOutButton)

        // Navigate to the stats page
        statPageButton.setOnClickListener {
            startActivity(Intent(this, DirectorStatPageActivity::class.java))
        }

        // Navigate to the user management page
        viewProfilesButton.setOnClickListener {
            startActivity(Intent(this, ViewProfilesActivity::class.java))
        }

        // Navigate to the feedback page
        feedbackButton.setOnClickListener {
            startActivity(Intent(this, ViewFeedbackActivity::class.java))
        }

        // Sign out functionality - directly log out
        signOutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}