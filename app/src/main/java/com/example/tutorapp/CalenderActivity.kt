package com.example.tutorapp

import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class CalenderActivity : AppCompatActivity() {

    private lateinit var eventListView: ListView
    private lateinit var currentUserId: String
    private val enrollmentsRef = FirebaseDatabase.getInstance().reference.child("student_tutor_enrollments")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        // Initialize SharedPreferences
        UserPreferences.init(this)

        // Get current user ID from SharedPreferences
        currentUserId = UserPreferences.getLoggedInUserId() ?: ""

        // Back button logic
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            onBackPressed()  // Go back to the previous activity
        }

        // Set up the ListView to display enrollments
        eventListView = findViewById(R.id.event_list)

        // Setup CalendarView
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDateToLong(year, month + 1, dayOfMonth)  // Convert to Long timestamp
            fetchEnrollmentsForDate(selectedDate)  // Update the enrollments for the selected date
        }

        // Fetch the student's enrollments for today's date initially
        if (currentUserId.isNotEmpty()) {
            fetchEnrollmentsForDate(getTodayDateInLong()) // Show today's enrollments by default
        } else {
            Toast.makeText(this, "You need to log in to view your enrollments.", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch enrollments for the selected date
    private fun fetchEnrollmentsForDate(dateInLong: Long) {
        // Normalize the selected date to remove the time part
        val normalizedDateInLong = normalizeDate(dateInLong)

        // Query the database to get the current user's enrollments for the selected date
        enrollmentsRef.orderByChild("studentId").equalTo(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val enrollments = mutableListOf<Enrollment>()
                if (snapshot.exists()) {
                    for (enrollmentSnapshot in snapshot.children) {
                        val enrollment = enrollmentSnapshot.getValue(Enrollment::class.java)
                        // Only proceed if enrollment is not null
                        if (enrollment != null && normalizeDate(enrollment.date) == normalizedDateInLong) {
                            enrollments.add(enrollment)
                        }
                    }

                    // Update the ListView with the enrollments
                    if (enrollments.isNotEmpty()) {
                        val adapter = EnrollmentAdapter(this@CalenderActivity, enrollments)
                        eventListView.adapter = adapter
                    } else {
                        Toast.makeText(this@CalenderActivity, "No enrollments found for this date.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CalenderActivity, "No enrollments found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CalenderActivity, "Error fetching enrollments: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Normalize the date to only the day part (removes the time)
    private fun normalizeDate(dateInLong: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInLong
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Convert the selected date (year, month, day) to Long timestamp format
    private fun formatDateToLong(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, dayOfMonth)  // Adjust for 0-based months
        return calendar.timeInMillis  // Return the timestamp in milliseconds
    }

    // Get today's date in Long timestamp format
    private fun getTodayDateInLong(): Long {
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis  // Return today's timestamp
    }
}