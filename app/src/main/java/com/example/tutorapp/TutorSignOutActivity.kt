package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TutorSignOutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnYes = findViewById<Button>(R.id.yes_button)
        btnYes.setOnClickListener {
            val intent = Intent(this@TutorSignOutActivity,LoginActivity::class.java)
            startActivity(intent)
        }
        val btnNo = findViewById<Button>(R.id.no_button)
        btnNo.setOnClickListener {
            val intent = Intent(this@TutorSignOutActivity,TutorDashboardNavBar::class.java)
            startActivity(intent)
        }

    }
}