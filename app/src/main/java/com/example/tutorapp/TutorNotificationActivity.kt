package com.example.tutorapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class TutorNotificationActivity : AppCompatActivity() {

    private lateinit var eventListView: ListView
    private lateinit var currentUserId: String
    private val enrollmentsRef = FirebaseDatabase.getInstance().reference.child("student_tutor_enrollments")
    private val usersRef = FirebaseDatabase.getInstance().reference.child("Users")  // Reference to the Users database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize SharedPreferences to get the current user ID
        UserPreferences.init(this)

        // Get current user ID from SharedPreferences
        currentUserId = UserPreferences.getLoggedInUserId() ?: ""

        // Back button logic
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            onBackPressed()  // Go back to the previous activity
        }

        // Set title for the notifications section
        val eventsTitle = findViewById<TextView>(R.id.events_title)
        eventsTitle.text = "My Enrollments"

        // Set up the ListView to display enrollments
        eventListView = findViewById(R.id.event_list)

        // Fetch the user's full name and enrollments
        if (currentUserId.isNotEmpty()) {
            fetchUserFullNameAndEnrollments()
        } else {
            Toast.makeText(this, "You need to log in to view your enrollments.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserFullNameAndEnrollments() {
        // Query the Users database to get the fullName for the current user
        usersRef.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullName = snapshot.child("fullName").getValue(String::class.java)

                if (fullName != null) {
                    // Now that we have the full name, let's fetch the enrollments
                    fetchEnrollmentsForTutor(fullName)
                } else {
                    Toast.makeText(this@TutorNotificationActivity, "Error fetching user data.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TutorNotificationActivity, "Error fetching user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchEnrollmentsForTutor(fullName: String) {
        // Query the enrollments to find records where tutorId matches the fullName
        enrollmentsRef.orderByChild("tutorId").equalTo(fullName).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val enrollments = mutableListOf<Enrollment>()
                if (snapshot.exists()) {
                    for (enrollmentSnapshot in snapshot.children) {
                        val enrollment = enrollmentSnapshot.getValue(Enrollment::class.java)
                        enrollment?.let { enrollments.add(it) }
                    }

                    // Set up the ListView with the data
                    val adapter = StudentProfileAdapter(this@TutorNotificationActivity, enrollments)
                    eventListView.adapter = adapter
                } else {
                    Toast.makeText(this@TutorNotificationActivity, "You have no enrollments.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TutorNotificationActivity, "Error fetching enrollments: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
