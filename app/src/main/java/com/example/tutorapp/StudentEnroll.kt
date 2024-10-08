package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StudentEnroll : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tutors_found)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            val intent = Intent(this@StudentEnroll,StudentSearch::class.java)
            startActivity(intent)
        }

        val btnHome = findViewById<ImageButton>(R.id.home_button)
        btnHome.setOnClickListener {
            val intent = Intent(this@StudentEnroll,DashboardNavBar::class.java)
            startActivity(intent)
        }

        val btnTutors = findViewById<Button>(R.id.tutor)
        btnTutors.setOnClickListener {
            val intent = Intent(this@StudentEnroll,TutorProfile::class.java)
            startActivity(intent)
        }

    }
}