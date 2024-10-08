package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TutorProfileLogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tutor_own_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnHomeScr = findViewById<ImageButton>(R.id.edit_profile)
        btnHomeScr.setOnClickListener {
            val intent = Intent(this@TutorProfileLogIn,EditTutorProfile::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            val intent = Intent(this@TutorProfileLogIn,TutorDashboardNavBar::class.java)
            startActivity(intent)
        }

    }
}