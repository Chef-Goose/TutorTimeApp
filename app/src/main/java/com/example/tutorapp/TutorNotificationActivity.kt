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

        // Fetch the student's enrollments from Firebase
        if (currentUserId.isNotEmpty()) {
            fetchEnrollments()
        } else {
            Toast.makeText(this, "You need to log in to view your enrollments.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchEnrollments() {
        // Query the database to get the current user's enrollments
        enrollmentsRef.orderByChild("tutorId").equalTo(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val enrollments = mutableListOf<Enrollment>()
                if (snapshot.exists()) {
                    for (enrollmentSnapshot in snapshot.children) {
                        val enrollment = enrollmentSnapshot.getValue(Enrollment::class.java)
                        enrollment?.let { enrollments.add(it) }
                    }

                    // Set up the ListView with the data
                    val adapter = EnrollmentAdapter(this@TutorNotificationActivity, enrollments)
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
