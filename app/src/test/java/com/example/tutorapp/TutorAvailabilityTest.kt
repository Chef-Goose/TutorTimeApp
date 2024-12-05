package com.example.tutorapp

import org.junit.Assert.assertEquals
import org.junit.Test

class TutorAvailabilityTest {

    @Test
    fun testTutorAvailability() {
        // Create an instance of TutorAvailability with all properties
        val tutorAvailability = TutorAvailability(
            course = "Math 101",
            timeSlot = "10:00 AM - 12:00 PM",
            certificate = "Certified Math Tutor",
            date = 1698578400000L, // Example timestamp
            name = "John Doe"
        )

        // Check if the properties are set correctly
        assertEquals("Math 101", tutorAvailability.course)
        assertEquals("10:00 AM - 12:00 PM", tutorAvailability.timeSlot)
        assertEquals("Certified Math Tutor", tutorAvailability.certificate)
        assertEquals(1698578400000L, tutorAvailability.date)
        assertEquals("John Doe", tutorAvailability.name)
    }

    @Test
    fun testEmptyConstructor() {
        // Create an instance with the default constructor (empty values)
        val tutorAvailability = TutorAvailability()

        // Check if the default values are empty or zero
        assertEquals("", tutorAvailability.course)
        assertEquals("", tutorAvailability.timeSlot)
        assertEquals("", tutorAvailability.certificate)
        assertEquals(0L, tutorAvailability.date)
        assertEquals("", tutorAvailability.name)
    }

    @Test
    fun testDateConversion() {
        // Use a specific date value
        val date = 1698578400000L
        val tutorAvailability = TutorAvailability(date = date)

        // Check if the date is stored correctly
        assertEquals(date, tutorAvailability.date)
    }
}
