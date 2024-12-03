package com.example.tutorapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import android.widget.ImageButton
import android.content.Intent

class StudentProfileforview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_profile)

        // Retrieve the studentId (passed via the Intent)
        val studentId = intent.getStringExtra("studentId")  // Ensure that "studentId" is passed when navigating
        if (studentId != null && studentId.isNotEmpty()) {
            fetchStudentProfile(studentId)
        } else {
            Toast.makeText(this, "Invalid student ID", Toast.LENGTH_SHORT).show()
        }

        // Handle the Back Button Click
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()  // This will call the system's back behavior
        }

        // Handle the Home Button Click
        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            // Navigate to the main activity (home screen)
            val intent = Intent(this, TutorDashboardNavBar::class.java)  // Replace with your home activity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear previous activities
            startActivity(intent)
        }
    }

    private fun fetchStudentProfile(studentId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        // Query Firebase for the student where studentId matches
        val query = usersRef.orderByChild("id").equalTo(studentId)

        query.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Assuming there's only one student with that studentId
                val student = snapshot.children.firstOrNull()?.getValue(Users::class.java)
                student?.let {
                    // Display the student's data in the TextViews
                    val fullNameTextView = findViewById<TextView>(R.id.fullNameTextView)
                    val emailTextView = findViewById<TextView>(R.id.emailTextView)
                    val roleTextView = findViewById<TextView>(R.id.roleTextView)

                    fullNameTextView.text = it.fullName
                    emailTextView.text = it.email
                    roleTextView.text = it.role
                }
            } else {
                Toast.makeText(this@StudentProfileforview, "Student not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this@StudentProfileforview, "Error fetching student data: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
