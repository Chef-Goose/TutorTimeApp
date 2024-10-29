package com.example.tutorapp
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
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
            val intent = Intent(this@TutorAvailable,TutorDashboardNavBar::class.java)
            startActivity(intent)
        }

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        // Set a listener to get the selected date
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Create a string from the selected date
            val selectedDate = "$dayOfMonth/${month + 1}/$year"  // Month is zero-based
            println("Selected date: $selectedDate")
        }

        val btnSearch = findViewById<Button>(R.id.btn_StartSearch)
        btnSearch.setOnClickListener {

            val intent = Intent(this@TutorAvailable,TutorDashboardNavBar::class.java)
            startActivity(intent)
        }

        val btnEnterTime = findViewById<Button>(R.id.btn_applyUnit3)
        btnEnterTime.setOnClickListener{
            val intent = Intent(this@TutorAvailable,TutorTimeSelection::class.java)
            startActivity(intent)
           // val databaseSend = TimesDatabase()
            //databaseSend.writePersonForTime("10:00")
        }

    }
}