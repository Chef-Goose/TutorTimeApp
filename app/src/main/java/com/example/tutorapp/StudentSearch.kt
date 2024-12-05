package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.CalendarView
import android.widget.ImageButton
import java.util.Calendar

class StudentSearch : AppCompatActivity() {

    private lateinit var spinnerCourses: Spinner
    private lateinit var spinnerGrades: Spinner
    private lateinit var calendarView: CalendarView
    private lateinit var btnSearch: Button // Button for search action
    private lateinit var btnInstant: Button // Button for search action

    // To store the selected date in milliseconds
    private var selectedDateMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_choice)

        // Initialize views
        spinnerCourses = findViewById(R.id.spinner_courses)
        spinnerGrades = findViewById(R.id.spinner_levels)
        calendarView = findViewById(R.id.calendarView)
        btnSearch = findViewById(R.id.btn_StartSearch) // Initialize the Button
        btnInstant = findViewById(R.id.btn_InstantSearch)

        // Set up the CalendarView to listen for date changes
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Convert the selected year, month, and day to a Date object in milliseconds
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0, 0)  // Set time to midnight (00:00:00)
            calendar.set(Calendar.MILLISECOND, 0)

            // Store the selected date in milliseconds
            selectedDateMillis = calendar.timeInMillis
        }

        // Search button click listener
        btnSearch.setOnClickListener {
            val selectedCourse = spinnerCourses.selectedItem.toString()
            val selectedGrade = spinnerGrades.selectedItem.toString()
            val finalGrade = selectedGrade.split(" ")[1]

            if (selectedCourse != "Select Course" && selectedGrade != "Select Grade" && selectedDateMillis != 0L) {
                // Pass data to the next activity (StudentEnroll)
                val intent = Intent(this@StudentSearch, StudentEnroll::class.java)
                intent.putExtra("selectedCourse", selectedCourse)
                intent.putExtra("selectedDate", selectedDateMillis)
                intent.putExtra("selectedGrade",finalGrade)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select both a course and a date", Toast.LENGTH_SHORT).show()
            }
        }

       // btnInstant.setOnClickListener{
         //   val intent = Intent(this@StudentSearch, StudentInstantSession::class.java)
           // startActivity(intent)
        //}

        // Back Button logic
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            onBackPressed()  // Go back to the previous activity
        }
    }
}
