package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TutorTimeSelection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tutor_time_choice)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            val intent = Intent(this@TutorTimeSelection, TutorAvailable::class.java)
            startActivity(intent)
        }

        // Day spinner setup
        val daySpinner: Spinner = findViewById(R.id.day_spinner)
        val dayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.day_options, // Ensure this includes a placeholder like "Select Day"
            android.R.layout.simple_spinner_item
        )
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = dayAdapter

        // Time spinner setup
        val timeSpinner: Spinner = findViewById(R.id.timeSpinner)
        val timeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.time_options, // Ensure this includes a placeholder like "Select Time"
            android.R.layout.simple_spinner_item
        )
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = timeAdapter

        val btnApplyDay = findViewById<Button>(R.id.btn_apply_day)
        btnApplyDay.setOnClickListener {
            val selectedDayPosition = daySpinner.selectedItemPosition
            val selectedTimePosition = timeSpinner.selectedItemPosition

            // Check if both day and time selections are valid
            if (selectedDayPosition != 0 && selectedTimePosition != 0) {
                val selectedDay = daySpinner.selectedItem.toString()
                val selectedTime = timeSpinner.selectedItem.toString()

                // Display confirmation message with selected day and time
                Toast.makeText(this, "Selected day: $selectedDay, time: $selectedTime", Toast.LENGTH_SHORT).show()

                // Proceed with the selected day and time (e.g., save to database)
                val databaseSend = TimesDatabase()
                databaseSend.writePersonForTime("$selectedDay $selectedTime")
            } else {
                //Must click Apply Day Button to get the message
                // Show error messages for invalid selections
                if (selectedDayPosition == 0) {
                    Toast.makeText(this, "Please select a valid day before applying.", Toast.LENGTH_SHORT).show()
                }
                if (selectedTimePosition == 0) {
                    Toast.makeText(this, "Please select a valid time before applying.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
