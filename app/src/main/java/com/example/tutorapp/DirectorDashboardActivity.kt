package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DirectorDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director_dashboard)

        // Button to navigate to the Director Stat Page
        val statPageButton = findViewById<Button>(R.id.statPageButton)
        statPageButton.setOnClickListener {
            // Navigate to DirectorStatPageActivity (Create this activity)
            startActivity(Intent(this, DirectorStatPageActivity::class.java))
        }

        // Button to view all profiles
        val viewProfilesButton = findViewById<Button>(R.id.viewProfilesButton)
        viewProfilesButton.setOnClickListener {
            // Navigate to ViewProfilesActivity (Create this activity)
            startActivity(Intent(this, ViewProfilesActivity::class.java))
        }

        // Button to view user feedback
        val feedbackButton = findViewById<Button>(R.id.feedbackButton)
        feedbackButton.setOnClickListener {
            // Navigate to ViewFeedbackActivity (Create this activity)
            startActivity(Intent(this, ViewFeedbackActivity::class.java))
        }
    }
}