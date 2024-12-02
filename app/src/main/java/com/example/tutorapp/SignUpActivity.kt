package com.example.tutorapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tutorapp.ui.hashPassword




import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        database = FirebaseDatabase.getInstance().getReference("Users")


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
            val confirmPassword = findViewById<TextInputEditText>(R.id.ConfirmPassword).text.toString()

            // Check if all fields are filled
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user type is selected
            if (selectedId == -1) {
                Toast.makeText(this, "Please select whether you are a tutor or student", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check password length and confirm password
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hash the password before saving it to Firebase
            val hashedPassword = hashPassword(password)

            // Check if email already exists in Firebase
            database.orderByChild("email").equalTo(email).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
                    } else {
                        // Email does not exist; proceed with saving the new user
                        val userType = if (selectedId == R.id.radio_tutor) "tutor" else "student"
                        val concatId = database.push().key!!
                        val user = Users(concatId, fullName, email, hashedPassword, userType,false)

                        database.child(concatId).setValue(user).addOnSuccessListener {
                            Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                            startActivity(intent)

                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener { error ->
                    Toast.makeText(this, "Error checking email: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }


    }
}
