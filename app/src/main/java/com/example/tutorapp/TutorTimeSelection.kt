package com.example.tutorapp
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class TutorTimeSelection: AppCompatActivity() {
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
            val intent = Intent(this@TutorTimeSelection,TutorAvailabilityActivity::class.java)
            startActivity(intent)
        }

        val timeSpinner: Spinner = findViewById(R.id.timeSpinner)
        val adapter = ArrayAdapter.createFromResource(this,
            R.array.time_options, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = adapter

        val btnApplyMonday = findViewById<Button>(R.id.btn_Apply_Monday_Times)
        btnApplyMonday.setOnClickListener {
            val databaseSend = TimesDatabase()
            databaseSend.writePersonForTime("10:00")

        }

    }
}