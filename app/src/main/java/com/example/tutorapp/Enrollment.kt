package com.example.tutorapp

data class Enrollment(
    val enrollmentId: String = "",  // Adding this field to uniquely identify the enrollment
    val studentId: String = "",
    val tutorId: String = "",
    val course: String = "",
    val date: Long = 0L,
    val timeSlot: String = ""
)