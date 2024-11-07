package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class TutorDashboardNavBar : AppCompatActivity() {

    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_tutordashboard)

        // Initialize Firebase reference for users
        usersRef = FirebaseDatabase.getInstance().getReference("Users")

        // Initialize UserPreferences (IMPORTANT to do this before accessing SharedPreferences)
        UserPreferences.init(this)

        // Set up edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fetch the user ID from UserPreferences
        val userID = UserPreferences.getLoggedInUserId()

        if (userID != null) {
            Log.d("DashboardNavBar", "User ID: $userID")
            // Fetch the full name from Firebase
            usersRef.child(userID).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val userFullName = dataSnapshot.child("fullName").value?.toString() ?: "User"
                    // Update the welcome text view with the full name
                    val welcomeTextView = findViewById<TextView>(R.id.welcome_message)
                    welcomeTextView.text = "Welcome, $userFullName"  // Set the dynamic welcome message
                } else {
                    // If user data is not found in Firebase, display a generic message
                    val welcomeTextView = findViewById<TextView>(R.id.welcome_message)
                    welcomeTextView.text = "Welcome, User"
                    Log.e("DashboardNavBar", "User data not found in Firebase.")
                }
            }.addOnFailureListener { exception ->
                // Handle any errors when fetching the data
                Log.e("DashboardNavBar", "Error fetching user data", exception)
                val welcomeTextView = findViewById<TextView>(R.id.welcome_message)
                welcomeTextView.text = "Welcome, User"
                Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("DashboardNavBar", "User ID is null or not found in SharedPreferences.")
            // If no userID is found in SharedPreferences, display a generic message
            val welcomeTextView = findViewById<TextView>(R.id.welcome_message)
            welcomeTextView.text = "Welcome, User"
        }

        // Handle button clicks (Navigation to different activities)
        val btnBack = findViewById<ImageButton>(R.id.home_button)
        btnBack.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar, TutorDashboardNavBar::class.java)
            startActivity(intent)
        }

        // Sign Out button
        val btnSignOut = findViewById<ImageButton>(R.id.sign_out_button)
        btnSignOut.setOnClickListener {
            // Log out and clear the shared preferences
            UserPreferences.clearUserData()
            val intent = Intent(this@TutorDashboardNavBar, SignOutActivity::class.java)
            startActivity(intent)
        }

        // Profile button
        val btnProfileView = findViewById<ImageButton>(R.id.profile_button)
        btnProfileView.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar, TutorProfile::class.java)
            startActivity(intent)
        }

        // Calendar button
        val btnCalender = findViewById<ImageButton>(R.id.calender)
        btnCalender.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar, TutorCalenderActivity::class.java)
            startActivity(intent)
        }

        // Messages button
        val btnMessages = findViewById<ImageButton>(R.id.notification_button)
        btnMessages.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar, TutorMessagesActivity::class.java)
            startActivity(intent)
        }

        // Book button for subject booking
        val btnBook = findViewById<Button>(R.id.subject_button)
        btnBook.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar, TutorAvailabilityActivity::class.java)
            startActivity(intent)
        }

        // Notification button
        val btnNoti = findViewById<ImageButton>(R.id.notification)
        btnNoti.setOnClickListener {
            val intent = Intent(this@TutorDashboardNavBar, TutorNotificationActivity::class.java)
            startActivity(intent)
        }
    }
}
