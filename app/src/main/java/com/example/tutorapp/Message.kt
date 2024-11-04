package com.example.tutorapp

data class Message(
    val messageID: String = "", // Add this line
    val senderID: String = "",
    val receiverID: String = "",
    val message: String = "",
    val timestamp: String = ""
)
