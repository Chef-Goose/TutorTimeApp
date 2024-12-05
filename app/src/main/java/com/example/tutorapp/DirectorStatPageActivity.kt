package com.example.tutorapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class DirectorStatPageActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director_stat_page)

        val statsTextView = findViewById<TextView>(R.id.statsTextView)

        val backButton = findViewById<ImageButton>(R.id.back_button)

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Navigate back to the previous screen
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("student_tutor_enrollments")

        // Fetch stats for the current month
        fetchStatsForCurrentMonth { tutorStats ->
            // Display stats
            val statsBuilder = StringBuilder("Sessions Completed This Month:\n\n")
            for ((tutor, count) in tutorStats) {
                statsBuilder.append("$tutor: $count sessions\n")
            }
            statsTextView.text = statsBuilder.toString()
        }
    }

    private fun fetchStatsForCurrentMonth(onComplete: (Map<String, Int>) -> Unit) {
        val tutorStats = mutableMapOf<String, Int>()

        // Get the start and end timestamps for the current month
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        // Query Firebase for enrollments within the current month
        database.orderByChild("date")
            .startAt(startOfMonth.toDouble())
            .endAt(endOfMonth.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (enrollment in snapshot.children) {
                        val tutorId = enrollment.child("tutorId").getValue(String::class.java)
                        if (tutorId != null) {
                            tutorStats[tutorId] = tutorStats.getOrDefault(tutorId, 0) + 1
                        }
                    }
                    onComplete(tutorStats)
                }

                override fun onCancelled(error: DatabaseError) {
                    val statsTextView = findViewById<TextView>(R.id.statsTextView)
                    statsTextView.text = "Failed to load stats: ${error.message}"
                }
            })
    }
}