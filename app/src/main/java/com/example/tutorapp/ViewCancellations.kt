package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewCancellations: AppCompatActivity() {
    private lateinit var cancellationsRef: DatabaseReference
    private lateinit var cancellationsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancelledsessions)


        // Initialize Firebase Database reference
        cancellationsRef = FirebaseDatabase.getInstance().reference.child("cancelledAppointments")

        // Initialize TextView from layout
        cancellationsTextView = findViewById(R.id.cancelledTextView)

        // Fetch the feedbacks from Firebase
        readCancelledAppointments()

        // Back button functionality (optional)
        findViewById<View>(R.id.back_button).setOnClickListener {
            onBackPressed()  // Go back to previous screen
        }

        val btnDelete = findViewById<Button>(R.id.btnDelete)
        btnDelete.setOnClickListener {
            cancellationsRef.removeValue().addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Successfully Deleted The Cancelled Notifications",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun readCancelledAppointments() {
        // Retrieve the tutorId passed via intent
        val tutorId = intent.getStringExtra("tutorId")

        // Reference to the "cancelledAppointments" node in Firebase
        val cancelledAppointmentsRef = FirebaseDatabase.getInstance().reference.child("cancelledAppointments")

        cancelledAppointmentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cancelledAppointmentsList = StringBuilder() // StringBuilder to append all cancelled appointments
                for (appointmentSnapshot in snapshot.children) {
                    // Fetch the tutorId and feedbackText from the cancelled appointment
                    val appointmentTutorId = appointmentSnapshot.child("tutorId").getValue(String::class.java)
                    val appointmentDetails = appointmentSnapshot.child("status").getValue(String::class.java)

                    // Check if the tutorId matches the one passed in the intent
                    if (appointmentTutorId == tutorId) {
                        // If it matches, append the appointment details to the list
                        appointmentDetails?.let {
                            cancelledAppointmentsList.append(it).append("\n\n")
                        }
                    }
                }

                // Set the cancelled appointments to the TextView
                cancellationsTextView.text = cancelledAppointmentsList.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewCancellations, "Error reading cancelled appointments: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
