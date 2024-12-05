package com.example.tutorapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button

import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tutorapp.ui.hashPassword
import com.google.firebase.auth.FirebaseAuth




import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        database = FirebaseDatabase.getInstance().getReference("Users")
        firebaseAuth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        btnSignIn.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }


        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            val radioGroupUserType = findViewById<RadioGroup>(R.id.radioGroupUserType)
            val selectedId = radioGroupUserType.checkedRadioButtonId
            val fullName = findViewById<TextInputEditText>(R.id.inputFullName).text.toString()
            val email = findViewById<TextInputEditText>(R.id.inputEmail).text.toString()
            val password = findViewById<TextInputEditText>(R.id.inputPassword).text.toString()
            val confirmPassword =
                findViewById<TextInputEditText>(R.id.ConfirmPassword).text.toString()

            // Check if all fields are filled
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user type is selected
            if (selectedId == -1) {
                Toast.makeText(
                    this,
                    "Please select whether you are a tutor or student",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check password length and confirm password
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        firebaseAuth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    // Proceed with saving user data to Realtime Database
                                    val userType =
                                        if (selectedId == R.id.radio_tutor) "tutor" else "student"
                                    val userId = firebaseAuth.currentUser?.uid ?: ""
                                    val hashedPassword = hashPassword(password)

                                    val user = Users(userId, fullName, email, hashedPassword, userType,false)

                                    database.child(userId).setValue(user)
                                        .addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Sign-up successful! Please verify your email.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                // Navigate to login screen
                                                val intent = Intent(
                                                    this@SignUpActivity,
                                                    LoginActivity::class.java
                                                )
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Failed to save user data: ${dbTask.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to send verification email: ${emailTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {

                        // Handle registration failure
                        Toast.makeText(
                            this,
                            "Sign-up failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
        }
    }
}
