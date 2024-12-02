package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TutorOnboarding : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tutor_onboarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = FirebaseDatabase.getInstance().getReference("Users")

        val userID = intent.getStringExtra("user")
        val acceptCheckbox: CheckBox = findViewById(R.id.accept_checkbox)
        val continueButton: Button = findViewById(R.id.continue_button)

        continueButton.isEnabled = false
        acceptCheckbox.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            continueButton.isEnabled = isChecked
        }
        continueButton.setOnClickListener {
            val userRef = database.child(userID?:"")
            val updates = mapOf<String, Any>(
                "onboarding" to true
            )
            userRef.updateChildren(updates)
            val intent = Intent(this@TutorOnboarding,TutorDashboardNavBar::class.java)
                startActivity(intent)

        }
    }
}