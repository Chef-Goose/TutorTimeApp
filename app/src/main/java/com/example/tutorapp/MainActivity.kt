package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable offline persistence for Firebase Realtime Database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Delay to show splash screen and then navigate to LoginActivity
        Handler().postDelayed({
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
