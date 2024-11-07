package com.example.tutorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.CalendarView
import java.util.Calendar

class TutorAvailabilityActivity : AppCompatActivity() {

    private lateinit var spinnerCourses: Spinner
    private lateinit var spinnerTime: Spinner
    private lateinit var tbCertificate: TextInputEditText
    private lateinit var btnSaveAvailability: Button
    private lateinit var calendarView: CalendarView

    // Firebase Database References
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val availabilityRef: DatabaseReference = database.reference.child("tutor_availabilities")
    private val usersRef: DatabaseReference = database.reference.child("Users")  // Reference to the Users node

    // To store the selected date in milliseconds
    private var selectedDateMillis: Long = 0

    // Get the user ID from SharedPreferences
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_availability)

        // Initialize the UserPreferences to get the logged-in user ID
        UserPreferences.init(this)
        userID = UserPreferences.getLoggedInUserId() ?: ""

        // Initialize the views
        spinnerCourses = findViewById(R.id.spinner_courses)
        spinnerTime = findViewById(R.id.spinner_time)
        tbCertificate = findViewById(R.id.tb_certificate)
        btnSaveAvailability = findViewById(R.id.btn_saveAvailability)
        calendarView = findViewById(R.id.calendarView)

        // Set up the back button
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            finish()  // Finish the activity and go back
        }

        // Set up the save button to trigger the saveAvailability function
        btnSaveAvailability.setOnClickListener {
            saveAvailability()
        }

        // Set up the CalendarView to listen for date changes
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Convert the selected year, month, and day to a Date object in milliseconds
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0, 0)  // Set time to midnight (00:00:00)
            calendar.set(Calendar.MILLISECOND, 0)

            // Store the selected date in milliseconds
            selectedDateMillis = calendar.timeInMillis
        }
    }

    private fun saveAvailability() {
        val selectedCourse = spinnerCourses.selectedItem.toString()
        val selectedTime = spinnerTime.selectedItem.toString()
        val certificate = tbCertificate.text.toString()

        // Check if all required fields are filled and a date is selected
        if (selectedCourse == "Select Course" || selectedTime == "Select Time" || certificate.isEmpty() || selectedDateMillis == 0L) {
            Toast.makeText(this, "Please fill all fields and select a date", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch the logged-in user ID from SharedPreferences
        val userID = UserPreferences.getLoggedInUserId()

        // If userID is null, show an error and return
        if (userID.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a reference to the Users node in Firebase
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")

        // Fetch the user's full name using the userID
        usersRef.child(userID).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Get the full name of the logged-in user
                val fullName = dataSnapshot.child("fullName").value?.toString() ?: "Unknown User"

                // Log full name for debugging purposes
                Log.d("TutorAvailabilityActivity", "Full Name: $fullName")

                // Create a new TutorAvailability object with the full name and other details
                val tutorAvailability = TutorAvailability(
                    course = selectedCourse,
                    timeSlot = selectedTime,
                    certificate = certificate,
                    date = selectedDateMillis, // Use the selected date in milliseconds
                    name = fullName  // Store the full name
                )

                // Push the data to Firebase Realtime Database under the tutor_availabilities node
                val availabilityRef = FirebaseDatabase.getInstance().getReference("tutor_availabilities")
                val newAvailabilityRef = availabilityRef.push()
                newAvailabilityRef.setValue(tutorAvailability)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Availability Saved", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error saving availability", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("TutorAvailabilityActivity", "Error fetching user info", it)
            Toast.makeText(this, "Error fetching user info", Toast.LENGTH_SHORT).show()
        }
    }
}
