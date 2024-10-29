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

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Perform Firebase operation, e.g., send data to Firebase Realtime Database
                Toast.makeText(this, "Full Name: $fullName\nEmail: $email", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }


            if (selectedId != -1) {
                val radioButton = findViewById<RadioButton>(selectedId)




                val intent = if (radioButton.id == R.id.radio_tutor) {
                    val concatId = database.push().key!!
                    val User = Users(concatId,fullName, email, password, "tutor")
                    database.child(concatId).setValue(User).addOnSuccessListener {

                        Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener{
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

                    }
                    Intent(this@SignUpActivity, TutorLoginActivitytest::class.java)
                } else {
                    val concatId = database.push().key!!
                    val User = Users(concatId,fullName, email, password, "student")
                    database.child(concatId).setValue(User).addOnSuccessListener {

                        Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener{
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

                    }
                    Intent(this@SignUpActivity, LoginActivity::class.java)
                }
                startActivity(intent)
            }
        }
    }
}
