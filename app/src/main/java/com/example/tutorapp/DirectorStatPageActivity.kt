package com.example.tutorapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

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
        database = FirebaseDatabase.getInstance().getReference("Sessions") // Update with your sessions data node

        // Fetch stats from the database
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Assuming each child under "Sessions" represents a completed session
                var totalSessions = 0
                val sessionsPerTutor = mutableMapOf<String, Int>()

                for (session in snapshot.children) {
                    totalSessions++
                    val tutorId = session.child("tutorId").getValue(String::class.java)
                    if (tutorId != null) {
                        sessionsPerTutor[tutorId] = sessionsPerTutor.getOrDefault(tutorId, 0) + 1
                    }
                }

                val statsBuilder = StringBuilder()
                statsBuilder.append("Total Sessions Completed: $totalSessions\n\n")
                statsBuilder.append("Sessions Per Tutor:\n")

                for ((tutor, count) in sessionsPerTutor) {
                    statsBuilder.append("- Tutor $tutor: $count sessions\n")
                }

                statsTextView.text = statsBuilder.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                statsTextView.text = "Failed to load stats: ${error.message}"
            }
        })
    }
}