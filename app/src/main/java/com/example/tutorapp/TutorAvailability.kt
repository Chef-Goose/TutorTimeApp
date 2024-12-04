package com.example.tutorapp

data class TutorAvailability(
    val course: String = "",
    val timeSlot: String = "",
    val certificate: String = "",
    val date: Long = 0L,
    val name: String = "", // Add tutor name
    val gradeLevel: String = ""
)
