package com.example.tutorapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class StudentEnroll : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private val database = FirebaseDatabase.getInstance()
    private val availabilityRef = database.reference.child("tutor_availabilities")
    private val enrollmentsRef: DatabaseReference = database.reference.child("student_tutor_enrollments")
    private lateinit var currentUserId: String  // To hold the current student's ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutors_found)

        // Initialize SharedPreferences
        UserPreferences.init(this)

        // Get the selected course and date from the Intent
        val selectedCourse = intent.getStringExtra("selectedCourse")
        val selectedDate = intent.getLongExtra("selectedDate", 0L)

        tableLayout = findViewById(R.id.table)

        // Display selected course and date
        val courseTextView = findViewById<TextView>(R.id.tb_CourseName)
        val dateTextView = findViewById<TextView>(R.id.editTextDate)

        if (selectedCourse != null && selectedDate != 0L) {
            courseTextView.text = "Course: $selectedCourse"
            dateTextView.text = "Date: ${formatDate(selectedDate)}"
        } else {
            Toast.makeText(this, "Invalid course or date", Toast.LENGTH_SHORT).show()
        }

        // Get current user ID from SharedPreferences
        currentUserId = UserPreferences.getLoggedInUserId() ?: ""

        // Back button logic
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            onBackPressed()  // Go back to the previous activity
        }

        // Fetch tutors for the selected course and date
        if (selectedCourse != null) {
            getTutors(selectedCourse, selectedDate)
        }
    }

    private fun getTutors(course: String, date: Long) {
        availabilityRef.orderByChild("course").equalTo(course).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tableLayout.removeAllViews() // Clear previous results

                if (snapshot.exists()) {
                    var tutorFound = false
                    for (tutorSnapshot in snapshot.children) {
                        val tutorAvailability = tutorSnapshot.getValue(TutorAvailability::class.java)
                        if (tutorAvailability != null && tutorAvailability.date == date) {
                            addTutorToTable(tutorAvailability, tutorSnapshot.key!!)
                            tutorFound = true
                        }
                    }

                    if (!tutorFound) {
                        Toast.makeText(this@StudentEnroll, "No tutors available for this course and date.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@StudentEnroll, "No tutors available for this course.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StudentEnroll, "Error fetching tutors", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTutorToTable(tutor: TutorAvailability, tutorId: String) {
        val row = TableRow(this)

        // Create a TextView for the tutor's name
        val tutorName = TextView(this)
        tutorName.text = tutor.name
        tutorName.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        // Create a TextView for the tutor's certificate
        val certificates = TextView(this)
        certificates.text = tutor.certificate
        certificates.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        // Create a TextView for the tutor's time slot
        val timeSlot = TextView(this)
        timeSlot.text = tutor.timeSlot
        timeSlot.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        // Create a Button for each tutor (e.g., "Enroll" button)
        val enrollButton = Button(this)
        enrollButton.text = "Enroll"
        enrollButton.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        enrollButton.setOnClickListener {
            enrollStudentWithTutor(tutor, tutorId)
        }

        // Add all the views to the row
        row.addView(tutorName)
        row.addView(certificates)
        row.addView(timeSlot)
        row.addView(enrollButton)

        // Add the row to the TableLayout
        tableLayout.addView(row)
    }

    private fun enrollStudentWithTutor(tutor: TutorAvailability, tutorId: String) {
        if (currentUserId.isNotEmpty()) {
            // Create a new enrollment entry in the database
            val enrollment = mapOf(
                "studentId" to currentUserId,
                "tutorId" to tutorId,  // Use the tutor's unique ID
                "course" to tutor.course,
                "date" to tutor.date,
                "timeSlot" to tutor.timeSlot
            )

            // Add the enrollment to the "student_tutor_enrollments" node
            enrollmentsRef.push().setValue(enrollment)
                .addOnSuccessListener {
                    // Remove tutor availability from Firebase
                    removeTutorAvailability(tutorId)

                    Toast.makeText(this, "You have successfully enrolled with ${tutor.name}!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Enrollment failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "You need to log in to enroll.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeTutorAvailability(tutorId: String) {
        // Remove the tutor's availability from the "tutor_availabilities" node
        availabilityRef.child(tutorId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Tutor availability removed.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove tutor availability.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }
}
