package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.content.SharedPreferences
import com.example.tutorapp.ui.hashPassword
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Users")

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        btnSignIn.setOnClickListener {
            // Get user inputs
            val email = findViewById<EditText>(R.id.inputEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.inputPassword).text.toString().trim()

            // Check if fields are filled
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Authenticate user using Firebase Authentication
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val firebaseUser = firebaseAuth.currentUser

                        // Check if email is verified
                        if (firebaseUser != null && firebaseUser.isEmailVerified) {
                            // Fetch user details from Realtime Database
                            database.orderByChild("email").equalTo(email).get()
                                .addOnSuccessListener { dataSnapshot ->
                                    if (dataSnapshot.exists()) {
                                        for (userSnapshot in dataSnapshot.children) {
                                            val user = userSnapshot.getValue(Users::class.java)
                                            if (user != null) {
                                                Toast.makeText(
                                                    this,
                                                    "Login Successful",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                sharedPreferences.edit()
                                                    .putString("userID", user.id)
                                                    .apply()

                                                val intent = when (user.role) {
                                                    "tutor" -> {
                                                        if (!user.onboarding) {
                                                            val onboardingIntent = Intent(this@LoginActivity, TutorOnboarding::class.java)
                                                            onboardingIntent.putExtra("user", user.id)
                                                            onboardingIntent
                                                        } else {
                                                            Intent(this@LoginActivity, TutorDashboardNavBar::class.java)
                                                        }
                                                    }
                                                    "director" -> Intent(this@LoginActivity, DirectorDashboardActivity::class.java)
                                                    else -> Intent(this@LoginActivity, DashboardNavBar::class.java)
                                                }
                                                sharedPreferences.edit().putString("userID", user.id).apply()
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "User data not found",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(
                                        this,
                                        "Error: ${error.message}",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()

                                }
                        } else {
                            // Email not verified
                            Toast.makeText(
                                this,
                                "Please verify your email before logging in.",
                                Toast.LENGTH_LONG
                            ).show()
                            firebaseUser?.sendEmailVerification()
                                ?.addOnCompleteListener { resendTask ->
                                    if (resendTask.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Verification email sent again. Please check your inbox.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Failed to send verification email: ${resendTask.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    } else {
                        // Authentication failed
                        Toast.makeText(
                            this,
                            "Authentication failed: ${authTask.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

}