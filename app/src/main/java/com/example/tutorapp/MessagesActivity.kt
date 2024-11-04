package com.example.tutorapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class MessagesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private val messagesList = mutableListOf<Message>()
    private lateinit var currentUserID: String // Logged-in user ID
    private lateinit var tutorSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private var tutorList: List<Users> = emptyList()
    private var selectedTutorID: String = "" // Selected tutor ID
    private lateinit var messagesListener: ValueEventListener
    private lateinit var sentMessagesListener: ValueEventListener
    private var isUpdatingMessages = false // Flag to prevent duplicate loading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        // Initialize shared preferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        currentUserID = sharedPreferences.getString("userID", "") ?: ""

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Messages")

        // Initialize views
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        val sendButton = findViewById<Button>(R.id.send_button)
        val messageEditText = findViewById<EditText>(R.id.message_edit_text)
        recyclerView = findViewById(R.id.recyclerView)
        tutorSpinner = findViewById(R.id.tutor_spinner)

        // Set up RecyclerView
        setupRecyclerView()

        // Load tutors
        loadTutors()

        // Back button functionality
        btnBack.setOnClickListener {
            finish() // Go back to the previous activity
        }

        // Spinner item selection listener
        tutorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedTutorID = tutorList[position].id ?: "" // Get selected tutor ID
                loadMessages() // Load messages for the selected tutor
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedTutorID = "" // Default or handle case when nothing is selected
            }
        }

        // Send message functionality
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty() && selectedTutorID.isNotEmpty()) {
                sendMessage(messageText, selectedTutorID)
                messageEditText.text.clear() // Clear input field
            } else {
                Toast.makeText(this, "Please select a tutor and enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(messagesList, currentUserID)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = messageAdapter
    }

    private fun loadMessages() {
        messagesList.clear() // Clear current list
        Log.d("MessagesActivity", "Loading messages for tutor ID: $selectedTutorID")

        // Load messages where the current user is the receiver
        messagesListener = database.orderByChild("receiverID").equalTo(currentUserID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isUpdatingMessages) return // Prevent loading while sending a message

                messagesList.clear() // Clear previous messages

                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(Message::class.java)
                    if (message != null && message.senderID == selectedTutorID) {
                        messagesList.add(message) // Add received message
                        Log.d("MessagesActivity", "Received message: ${message.message}")
                    }
                }
                loadSentMessages() // Load sent messages after received messages
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessagesActivity", "Error loading messages: ${error.message}")
            }
        })
    }

    private fun loadSentMessages() {
        sentMessagesListener = database.orderByChild("senderID").equalTo(currentUserID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isUpdatingMessages) return // Prevent loading while sending a message

                val newMessages = mutableListOf<Message>()
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(Message::class.java)
                    if (message != null && message.receiverID == selectedTutorID) {
                        newMessages.add(message) // Collect sent messages
                        Log.d("MessagesActivity", "Sent message: ${message.message}")
                    }
                }

                // Add new messages to the list without duplicates
                messagesList.removeAll { msg -> newMessages.any { it.messageID == msg.messageID } }
                messagesList.addAll(newMessages)

                // Sort messages by timestamp
                messagesList.sortBy { it.timestamp }
                Log.d("MessagesActivity", "Total messages: ${messagesList.size}")
                messageAdapter.notifyDataSetChanged() // Notify adapter of new items
                recyclerView.scrollToPosition(messagesList.size - 1) // Scroll to the last message
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessagesActivity", "Error loading messages: ${error.message}")
            }
        })
    }

    private fun loadTutors() {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        usersRef.orderByChild("role").equalTo("tutor").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newTutorList = mutableListOf<Users>()
                for (snapshot in dataSnapshot.children) {
                    val tutor = snapshot.getValue(Users::class.java)
                    tutor?.let {
                        val tutorWithId = it.copy(id = snapshot.key)
                        newTutorList.add(tutorWithId)
                    }
                }
                tutorList = newTutorList

                // Populate Spinner
                val adapter = ArrayAdapter(this@MessagesActivity, android.R.layout.simple_spinner_item, tutorList.map { it.fullName })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                tutorSpinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessagesActivity, "Error loading tutors: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage(messageText: String, receiverID: String) {
        val messageID = database.push().key ?: return
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())

        val message = Message(
            messageID = messageID, // Assign the message ID here
            senderID = currentUserID,
            receiverID = receiverID,
            message = messageText,
            timestamp = timestamp // Store the timestamp as a formatted String
        )

        isUpdatingMessages = true // Set the flag to prevent duplicate loading
        // Save the message in the database
        database.child(messageID).setValue(message)
            .addOnSuccessListener {
                messagesList.add(message) // Add sent message to the list
                messageAdapter.notifyItemInserted(messagesList.size - 1) // Notify adapter of new item
                recyclerView.scrollToPosition(messagesList.size - 1) // Scroll to the last message
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error sending message: ${error.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                isUpdatingMessages = false // Reset the flag after sending
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.removeEventListener(messagesListener)
        database.removeEventListener(sentMessagesListener)
    }
}
