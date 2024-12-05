package com.example.tutorapp

import org.junit.Assert.*
import org.junit.Test

class EnrollmentTest {

    // Test the Enrollment constructor and field initialization
    @Test
    fun testEnrollmentConstructor() {
        val enrollment = Enrollment(
            enrollmentId = "E1234",
            studentId = "S5678",
            tutorId = "T9876",
            course = "Math 101",
            date = 1622547900000L,
            timeSlot = "9:00 AM - 10:00 AM"
        )

        assertEquals("E1234", enrollment.enrollmentId)
        assertEquals("S5678", enrollment.studentId)
        assertEquals("T9876", enrollment.tutorId)
        assertEquals("Math 101", enrollment.course)
        assertEquals(1622547900000L, enrollment.date)
        assertEquals("9:00 AM - 10:00 AM", enrollment.timeSlot)
    }

    // Test default constructor (all fields initialized with default values)
    @Test
    fun testEnrollmentDefaultConstructor() {
        val enrollment = Enrollment()

        assertEquals("", enrollment.enrollmentId)
        assertEquals("", enrollment.studentId)
        assertEquals("", enrollment.tutorId)
        assertEquals("", enrollment.course)
        assertEquals(0L, enrollment.date)
        assertEquals("", enrollment.timeSlot)
    }

    // Test for only essential fields (enrollmentId, studentId, tutorId, course)
    @Test
    fun testEnrollmentEssentialFields() {
        val enrollment = Enrollment(
            enrollmentId = "E1234",
            studentId = "S5678",
            tutorId = "T9876",
            course = "Math 101"
        )

        assertEquals("E1234", enrollment.enrollmentId)
        assertEquals("S5678", enrollment.studentId)
        assertEquals("T9876", enrollment.tutorId)
        assertEquals("Math 101", enrollment.course)
        assertEquals(0L, enrollment.date) // Default value for date
        assertEquals("", enrollment.timeSlot) // Default value for timeSlot
    }

    // Test enrollment with all default values except date and timeSlot
    @Test
    fun testEnrollmentWithDateAndTimeSlot() {
        val enrollment = Enrollment(
            date = 1622547900000L,
            timeSlot = "10:00 AM - 11:00 AM"
        )

        assertEquals("", enrollment.enrollmentId) // Default value
        assertEquals("", enrollment.studentId) // Default value
        assertEquals("", enrollment.tutorId) // Default value
        assertEquals("", enrollment.course) // Default value
        assertEquals(1622547900000L, enrollment.date)
        assertEquals("10:00 AM - 11:00 AM", enrollment.timeSlot)
    }
}
