package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
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

            if (selectedId != -1) {
                val radioButton = findViewById<RadioButton>(selectedId)
                val intent = if (radioButton.id == R.id.radio_tutor) {
                    Intent(this@SignUpActivity, TutorLoginActivitytest::class.java)
                } else {
                    Intent(this@SignUpActivity, LoginActivity::class.java)
                }
                startActivity(intent)
            }
        }
    }
}
