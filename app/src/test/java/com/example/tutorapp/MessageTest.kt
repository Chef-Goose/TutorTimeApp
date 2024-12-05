package com.example.tutorapp

import org.junit.Test
import kotlin.test.assertEquals

class MessageTest {

    @Test
    fun testMessageCreationWithDefaultValues() {
        // Create a Message object using default values
        val message = Message()

        // Assert that the default values are correctly set
        assertEquals("", message.messageID)
        assertEquals("", message.senderID)
        assertEquals("", message.receiverID)
        assertEquals("", message.message)
        assertEquals("", message.timestamp)
    }

    @Test
    fun testMessageCreationWithCustomValues() {
        // Create a Message object with custom values
        val message = Message(
            messageID = "12345",
            senderID = "user1",
            receiverID = "user2",
            message = "Hello, how are you?",
            timestamp = "2024-12-05T10:30:00"
        )

        // Assert that the values are correctly set
        assertEquals("12345", message.messageID)
        assertEquals("user1", message.senderID)
        assertEquals("user2", message.receiverID)
        assertEquals("Hello, how are you?", message.message)
        assertEquals("2024-12-05T10:30:00", message.timestamp)
    }

    @Test
    fun testMessageEquality() {
        // Create two Message objects with the same data
        val message1 = Message(
            messageID = "12345",
            senderID = "user1",
            receiverID = "user2",
            message = "Hello, how are you?",
            timestamp = "2024-12-05T10:30:00"
        )
        val message2 = Message(
            messageID = "12345",
            senderID = "user1",
            receiverID = "user2",
            message = "Hello, how are you?",
            timestamp = "2024-12-05T10:30:00"
        )

        // Assert that both messages are equal
        assertEquals(message1, message2)
    }

    @Test
    fun testMessageHashCode() {
        // Create two Message objects with the same data
        val message1 = Message(
            messageID = "12345",
            senderID = "user1",
            receiverID = "user2",
            message = "Hello, how are you?",
            timestamp = "2024-12-05T10:30:00"
        )
        val message2 = Message(
            messageID = "12345",
            senderID = "user1",
            receiverID = "user2",
            message = "Hello, how are you?",
            timestamp = "2024-12-05T10:30:00"
        )

        // Assert that the hashcodes of both messages are equal
        assertEquals(message1.hashCode(), message2.hashCode())
    }
}
