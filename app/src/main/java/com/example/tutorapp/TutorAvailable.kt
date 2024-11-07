package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TutorAvailable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tutor_availability)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            val intent = Intent(this@TutorAvailable, TutorDashboardNavBar::class.java)
            startActivity(intent)
        }

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"  // Month is zero-based
            println("Selected date: $selectedDate")
        }

        val btnSearch = findViewById<Button>(R.id.btn_StartSearch)
        btnSearch.setOnClickListener {
            val intent = Intent(this@TutorAvailable, TutorDashboardNavBar::class.java)
            startActivity(intent)
        }

        val btnEnterTime = findViewById<Button>(R.id.btn_applyUnit3)
        val timeslots = findViewById<EditText>(R.id.TimeSlots)

        btnEnterTime.setOnClickListener {
            val timeInput = timeslots.text.toString().trim()

            if (timeInput.isEmpty()) {
                // Show error if no time is entered
                timeslots.error = "Please enter a time."
            } else if (!isValidTimeFormat(timeInput)) {
                // Show error if the time format is invalid
                timeslots.error = "Please enter a valid time."
            } else {
                // Proceed to the next activity if time is in the correct format
                val intent = Intent(this@TutorAvailable, TutorTimeSelection::class.java)
                startActivity(intent)
            }
        }
    }

    // Helper function to check if the entered time is in a valid format (e.g., "8:00 AM" or "10:30 PM")
    private fun isValidTimeFormat(time: String): Boolean {
        val timePattern = Regex("^(0?[1-9]|1[0-2]):[0-5][0-9] (AM|PM)$", RegexOption.IGNORE_CASE)
        return timePattern.matches(time)
    }
}
