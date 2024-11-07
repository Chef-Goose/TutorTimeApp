// TutorAvailabilityAdapter.kt
package com.example.tutorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class TutorAvailabilityAdapter(private val availabilityList: List<TutorAvailability>) :
    RecyclerView.Adapter<TutorAvailabilityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tutor_availability, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val availability = availabilityList[position]
        holder.courseTextView.text = "Course: ${availability.course}"
        holder.timeSlotTextView.text = "Time Slot: ${availability.timeSlot}"
        holder.certificateTextView.text = "Certificate: ${availability.certificate}"

        // Format the date
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.dateTextView.text = "Date: ${dateFormatter.format(Date(availability.date))}"
    }

    override fun getItemCount(): Int = availabilityList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseTextView: TextView = view.findViewById(R.id.tv_course)
        val timeSlotTextView: TextView = view.findViewById(R.id.tv_time_slot)
        val certificateTextView: TextView = view.findViewById(R.id.tv_certificate)
        val dateTextView: TextView = view.findViewById(R.id.tv_date)
    }
}
