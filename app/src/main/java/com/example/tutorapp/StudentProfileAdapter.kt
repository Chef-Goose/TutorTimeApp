package com.example.tutorapp

import android.content.Context
import android.content.Intent
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

class StudentProfileAdapter(
    private val context: Context,
    private val enrollments: MutableList<Enrollment>
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
        val view = inflater.inflate(R.layout.profile_student_item, null)

        val courseTextView = view.findViewById<TextView>(R.id.enrollment_course)
        val timeSlotTextView = view.findViewById<TextView>(R.id.enrollment_time_slot)
        val dateTextView = view.findViewById<TextView>(R.id.enrollment_date)
        val cancelButton = view.findViewById<Button>(R.id.btnCancelEnrollment)
        val viewTutorButton = view.findViewById<Button>(R.id.btnViewTutorProfile)

        courseTextView.text = enrollment.course
        timeSlotTextView.text = enrollment.timeSlot
        dateTextView.text = formatDate(enrollment.date)

        cancelButton.setOnClickListener {
            cancelEnrollment(enrollment, position)
        }

        // Navigate to TutorProfile with the tutorId
        viewTutorButton.setOnClickListener {
            val intent = Intent(context, StudentProfileforview::class.java)
            intent.putExtra("studentId", enrollment.studentId)  // Pass the studentId
            context.startActivity(intent)
        }

        return view
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }

    private fun cancelEnrollment(enrollment: Enrollment, position: Int) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("student_tutor_enrollments")

        val enrollmentRef = databaseRef.orderByChild("studentId")
            .equalTo(enrollment.studentId)
            .limitToFirst(1)

        enrollmentRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                if (snapshot.exists()) {
                    for (enrollmentSnapshot in snapshot.children) {
                        if (enrollmentSnapshot.child("course").value == enrollment.course) {
                            enrollmentSnapshot.ref.removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        enrollments.removeAt(position)
                                        notifyDataSetChanged()
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
