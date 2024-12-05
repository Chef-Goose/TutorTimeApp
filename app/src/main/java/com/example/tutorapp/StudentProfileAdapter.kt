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
}