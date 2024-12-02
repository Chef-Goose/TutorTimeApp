package com.example.tutorapp

data class Users(val id: String? = null,val fullName : String? = null,val email : String? = null, val password : String? = null, val role : String? = null, val onboarding : Boolean = false) {
    data class Appointment(
        val date: String,
        val times: List<String>
    )
}