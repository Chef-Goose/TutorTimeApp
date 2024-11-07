package com.example.tutorapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class EnrollmentAdapter(
    private val context: Context,
    private val enrollments: MutableList<Enrollment>  // Using a mutable list to allow updates
) : BaseAdapter() {

    override fun getCount(): Int {
        return enrollments.size
    }

    override fun getItem(position: Int): Any {
        return enrollments[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val enrollment = getItem(position) as Enrollment
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.enrollment_item, null)

        // Set the data for each enrollment item
        val courseTextView = view.findViewById<TextView>(R.id.enrollment_course)
        val timeSlotTextView = view.findViewById<TextView>(R.id.enrollment_time_slot)
        val dateTextView = view.findViewById<TextView>(R.id.enrollment_date)
        val cancelButton = view.findViewById<Button>(R.id.btnCancelEnrollment)

        courseTextView.text = enrollment.course
        timeSlotTextView.text = enrollment.timeSlot
        dateTextView.text = formatDate(enrollment.date)

        // Handle the cancel button click
        cancelButton.setOnClickListener {
            cancelEnrollment(enrollment, position)
        }

        return view
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }

    // Function to handle the canceling of an enrollment
    private fun cancelEnrollment(enrollment: Enrollment, position: Int) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("student_tutor_enrollments")

        // Query Firebase to find and delete the enrollment
        val enrollmentRef = databaseRef.orderByChild("studentId")
            .equalTo(enrollment.studentId)
            .limitToFirst(1)

        enrollmentRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                if (snapshot.exists()) {
                    for (enrollmentSnapshot in snapshot.children) {
                        if (enrollmentSnapshot.child("course").value == enrollment.course) {
                            // Delete the enrollment from Firebase
                            enrollmentSnapshot.ref.removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Successfully removed, so remove from the local list and update the adapter
                                        enrollments.removeAt(position)
                                        notifyDataSetChanged()  // Notify the adapter that the data has changed
                                        Toast.makeText(context, "Enrollment canceled successfully.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to cancel enrollment.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                } else {
                    Toast.makeText(context, "Enrollment not found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(context, "Error canceling enrollment: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
