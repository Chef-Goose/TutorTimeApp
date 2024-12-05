package com.example.tutorapp

import org.junit.Assert.assertEquals
import org.junit.Test

class UsersTest {

    // Test for Users class constructor and properties
    @Test
    fun testUsersConstructor() {
        val user = Users(
            id = "1",
            fullName = "John Doe",
            email = "john.doe@example.com",
            password = "password123",
            role = "admin",
            onboarding = true
        )

        assertEquals("1", user.id)
        assertEquals("John Doe", user.fullName)
        assertEquals("john.doe@example.com", user.email)
        assertEquals("password123", user.password)
        assertEquals("admin", user.role)
        assertEquals(true, user.onboarding)
    }

    // Test for default constructor (with null values)
    @Test
    fun testUsersDefaultConstructor() {
        val user = Users()

        assertEquals(null, user.id)
        assertEquals(null, user.fullName)
        assertEquals(null, user.email)
        assertEquals(null, user.password)
        assertEquals(null, user.role)
        assertEquals(false, user.onboarding)
    }

    // Test for Appointment nested class
    @Test
    fun testAppointmentConstructor() {
        val appointment = Users.Appointment(
            date = "2024-12-05",
            times = listOf("10:00 AM", "2:00 PM", "4:00 PM")
        )

        assertEquals("2024-12-05", appointment.date)
        assertEquals(3, appointment.times.size)
        assertEquals("10:00 AM", appointment.times[0])
        assertEquals("2:00 PM", appointment.times[1])
        assertEquals("4:00 PM", appointment.times[2])
    }

    // Test Appointment with empty list
    @Test
    fun testAppointmentWithEmptyTimes() {
        val appointment = Users.Appointment(
            date = "2024-12-05",
            times = emptyList()
        )

        assertEquals("2024-12-05", appointment.date)
        assertEquals(0, appointment.times.size)
    }

    // Test Appointment with only one time
    @Test
    fun testAppointmentWithOneTime() {
        val appointment = Users.Appointment(
            date = "2024-12-05",
            times = listOf("10:00 AM")
        )

        assertEquals("2024-12-05", appointment.date)
        assertEquals(1, appointment.times.size)
        assertEquals("10:00 AM", appointment.times[0])
    }

    // Test for the Appointment class when using different dates
    @Test
    fun testAppointmentDifferentDate() {
        val appointment = Users.Appointment(
            date = "2024-12-06",
            times = listOf("9:00 AM", "11:00 AM")
        )

        assertEquals("2024-12-06", appointment.date)
        assertEquals(2, appointment.times.size)
    }
}
