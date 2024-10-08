package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TutorDashboardNavBar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutordashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnHomeScr = findViewById<ImageButton>(R.id.home_button)
        btnHomeScr.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar,TutorDashboardNavBar::class.java)
            startActivity(intent)
        }
        val btnSignOut = findViewById<ImageButton>(R.id.sign_out_button)
        btnSignOut.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar,TutorSignOutActivity::class.java)
            startActivity(intent)
        }

        val btnProfileView = findViewById<ImageButton>(R.id.profile_button)
        btnProfileView.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar,TutorProfileLogIn::class.java)
            startActivity(intent)
        }

        val btnCalender = findViewById<ImageButton>(R.id.calender)
        btnCalender.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar,TutorCalenderActivity::class.java)
            startActivity(intent)
        }

        val btnMessages = findViewById<ImageButton>(R.id.notification_button)
        btnMessages.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar,TutorMessagesActivity::class.java)
            startActivity(intent)
        }

    }
}