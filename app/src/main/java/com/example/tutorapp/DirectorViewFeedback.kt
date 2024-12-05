package com.example.tutorapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DirectorViewFeedback: AppCompatActivity() {
    private lateinit var feedbackRef: DatabaseReference
    private lateinit var feedbackTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewfeedback)


        // Initialize Firebase Database reference
        feedbackRef = FirebaseDatabase.getInstance().reference.child("feedbacks")

        // Initialize TextView from layout
        feedbackTextView = findViewById(R.id.feedTextView)

        // Fetch the feedbacks from Firebase
        readFeedbacks()

        // Back button functionality (optional)
        findViewById<View>(R.id.back_button).setOnClickListener {
            onBackPressed()  // Go back to previous screen
        }
    }

    private fun readFeedbacks() {
        feedbackRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedbackList = StringBuilder() // StringBuilder to append all feedbacks
                for (feedbackSnapshot in snapshot.children) {
                    val feedbackText = feedbackSnapshot.child("feedbackText").getValue(String::class.java)
                    feedbackText?.let {
                        // Append each feedback with a newline
                        feedbackList.append(it).append("\n\n")
                    }
                }

                // Set the feedbacks to the TextView
                feedbackTextView.text = feedbackList.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DirectorViewFeedback, "Error reading feedbacks: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
