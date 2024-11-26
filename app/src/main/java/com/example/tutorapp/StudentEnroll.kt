package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.paypal.android.corepayments.*
import com.paypal.android.paypalwebpayments.*
import com.example.tutorapp.PayPalOrderCreator.*


import java.text.SimpleDateFormat
import java.util.*



class StudentEnroll : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private val database = FirebaseDatabase.getInstance()
    private val availabilityRef = database.reference.child("tutor_availabilities")
    private val enrollmentsRef: DatabaseReference = database.reference.child("student_tutor_enrollments")
    private lateinit var currentUserId: String  // To hold the current student's ID
    private lateinit var payPalWebCheckoutClient: PayPalWebCheckoutClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutors_found)


        // Initialize SharedPreferences
        UserPreferences.init(this)

        // Get the selected course and date from the Intent
        val selectedCourse = intent.getStringExtra("selectedCourse")
        val selectedDate = intent.getLongExtra("selectedDate", 0L)

        tableLayout = findViewById(R.id.table)

        // Display selected course and date
        val courseTextView = findViewById<TextView>(R.id.tb_CourseName)
        val dateTextView = findViewById<TextView>(R.id.editTextDate)

        if (selectedCourse != null && selectedDate != 0L) {
            courseTextView.text = "Course: $selectedCourse"
            dateTextView.text = "Date: ${formatDate(selectedDate)}"
        } else {
            Toast.makeText(this, "Invalid course or date", Toast.LENGTH_SHORT).show()
        }

        // Get current user ID from SharedPreferences
        currentUserId = UserPreferences.getLoggedInUserId() ?: ""

        // Back button logic
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            onBackPressed()  // Go back to the previous activity
        }
        val coreConfig = CoreConfig(getString(R.string.paypal_client_id), Environment.SANDBOX)
       payPalWebCheckoutClient =
            PayPalWebCheckoutClient(this, coreConfig, "com.example.tutorapp://paypal")

        // Fetch tutors for the selected course and date
        if (selectedCourse != null) {
            getTutors(selectedCourse, selectedDate)
        }
    }


    private fun getTutors(course: String, date: Long) {
        availabilityRef.orderByChild("course").equalTo(course)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tableLayout.removeAllViews() // Clear previous results

                    if (snapshot.exists()) {
                        var tutorFound = false
                        for (tutorSnapshot in snapshot.children) {
                            val tutorAvailability =
                                tutorSnapshot.getValue(TutorAvailability::class.java)
                            if (tutorAvailability != null && tutorAvailability.date == date) {
                                addTutorToTable(tutorAvailability)
                                tutorFound = true
                            }
                        }

                        if (!tutorFound) {
                            Toast.makeText(
                                this@StudentEnroll,
                                "No tutors available for this course and date.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@StudentEnroll,
                            "No tutors available for this course.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@StudentEnroll, "Error fetching tutors", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun addTutorToTable(tutor: TutorAvailability) {
        val row = TableRow(this)

        // Create a TextView for the tutor's name
        val tutorName = TextView(this)
        tutorName.text = tutor.name
        tutorName.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        // Create a TextView for the tutor's certificate
        val certificates = TextView(this)
        certificates.text = tutor.certificate
        certificates.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        // Create a TextView for the tutor's time slot
        val timeSlot = TextView(this)
        timeSlot.text = tutor.timeSlot
        timeSlot.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        // Create a Button for each tutor (e.g., "Enroll" button)
        val enrollButton = Button(this)
        enrollButton.text = "Enroll"
        enrollButton.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        enrollButton.setOnClickListener {
            enrollStudentWithTutor(tutor)
        }

        // Add all the views to the row
        row.addView(tutorName)
        row.addView(certificates)
        row.addView(timeSlot)
        row.addView(enrollButton)

        // Add the row to the TableLayout
        tableLayout.addView(row)
    }


    private fun startPayPalPayment(
        amount: String,
        currency: String,
        onPaymentSuccess: () -> Unit,
        onPaymentFailure: () -> Unit
    ) {
        try {
            // Generate a unique order ID
            val returnUrl = "com.example.tutorapp://paypal"
            val orderCreator = PayPalOrderCreator()
            val orderId = orderCreator.createOrder(amount, currency, returnUrl) // Create the order

            // Set the funding source (PayPal as default)
            val fundingSource = PayPalWebCheckoutFundingSource.PAYPAL


            // Create the PayPalWebCheckoutRequest
            val checkoutRequest = PayPalWebCheckoutRequest(
                orderId = orderId.toString(),
                fundingSource = fundingSource,

                )

            // Start the payment process with PayPalWebCheckoutClient
            payPalWebCheckoutClient.start(
                this, // activity context
                checkoutRequest
            )

            // Set the listener to handle the result of the checkout process
            payPalWebCheckoutClient.listener = object : PayPalWebCheckoutListener {
                override fun onPayPalWebSuccess(result: PayPalWebCheckoutResult) {
                    // Check if the result contains both orderId and payerId
                    if (result.orderId != null && result.payerId != null) {
                        // Payment was successful
                        Toast.makeText(
                            this@StudentEnroll,
                            "Payment successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onPaymentSuccess() // Call the success callback
                    } else {
                        // Handle case where result doesn't have both orderId and payerId
                        Toast.makeText(
                            this@StudentEnroll,
                            "Payment failed: Missing orderId or payerId.",
                            Toast.LENGTH_SHORT
                        ).show()
                        onPaymentFailure() // Call the failure callback
                    }
                }

                // Handle cancellation or errors
                override fun onPayPalWebFailure(error: PayPalSDKError) {
                    Toast.makeText(
                        this@StudentEnroll,
                        "Payment error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onPaymentFailure() // Call the failure callback
                }

                override fun onPayPalWebCanceled() {
                    Toast.makeText(this@StudentEnroll, "Payment canceled.", Toast.LENGTH_SHORT)
                        .show()
                    onPaymentFailure() // Call the failure callback
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error starting payment: ${e.message}", Toast.LENGTH_SHORT).show()
            onPaymentFailure() // Handle payment initialization failure
        }
    }

    // Example of a method to generate a unique order ID (you may want to generate this on your backend)
    private fun generateUniqueOrderId(): String {
        return "order_${System.currentTimeMillis()}"
    }

    private fun enrollStudentWithTutor(tutor: TutorAvailability) {
        if (currentUserId.isNotEmpty()) {
            // Start the payment process before enrollment

            startPayPalPayment(
                amount = "40.00", // Replace with the tutor's fee
                currency = "CAD",
                onPaymentSuccess = {
                    // Proceed with enrollment only after payment is successful
                    val enrollment = mapOf(
                        "studentId" to currentUserId,
                        "tutorId" to tutor.name,  // Assuming you have the tutor's ID in the TutorAvailability object
                        "course" to tutor.course,
                        "date" to tutor.date,
                        "timeSlot" to tutor.timeSlot
                    )

                    enrollmentsRef.push().setValue(enrollment)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "You have successfully enrolled with ${tutor.name}!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Enrollment failed. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                },
                onPaymentFailure = {
                    Toast.makeText(
                        this,
                        "Payment failed. Enrollment not completed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        } else {
            Toast.makeText(this, "You need to log in to enroll.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }


    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(intent)
        intent = newIntent
    }
}