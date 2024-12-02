package com.example.tutorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.paypal.android.corepayments.*
import com.paypal.android.paypalwebpayments.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException


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
            val paypal_client_id = "AeXROYuQMXFFQ7H99Qghs07CqXiU1bnzgoc2OlPDzKB4-7J3UoughuHzQ_kysmtCRQust1tpxc2tpsv_"
            val coreConfig = CoreConfig(paypal_client_id, Environment.SANDBOX)
            payPalWebCheckoutClient =
                PayPalWebCheckoutClient(this@StudentEnroll, coreConfig, "com.example.tutorapp://paypal")
            // Generate a unique order ID
            val returnUrl = "com.example.tutorapp://paypal"
            val orderCreator = PayPalOrderCreator()

            orderCreator.createOrder(amount, currency, returnUrl) { orderId ->
                if (orderId != null && orderId.isNotEmpty()) {
                    println("Order created successfully. Order ID: $orderId")
                    val checkoutRequest = PayPalWebCheckoutRequest(orderId.toString(), fundingSource = PayPalWebCheckoutFundingSource.PAYPAL)
                    payPalWebCheckoutClient.start(
                        activity = this,
                        request = checkoutRequest
                    )
                    // Set the listener to handle the result of the checkout process
                    payPalWebCheckoutClient.listener = object : PayPalWebCheckoutListener {
                        override fun onPayPalWebSuccess(result: PayPalWebCheckoutResult) {
                            // Check if the result contains both orderId and payerId
                            if (result.orderId != null) {

                                onPaymentSuccess()
                                // Payment was successful
                                Toast.makeText(
                                    this@StudentEnroll,
                                    "Payment successful!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                 // Call the success callback
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
                    // Proceed with the next steps like creating the PayPalWebCheckoutRequest
                } else {
                    println("Failed to create PayPal order.")
                }
            }








        } catch (e: Exception) {
            Toast.makeText(this, "Error starting payment: ${e.message}", Toast.LENGTH_SHORT).show()
            onPaymentFailure() // Handle payment initialization failure
        }
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
        super.onNewIntent(newIntent)
        intent = newIntent

        val data = intent?.data
        val opType = data?.getQueryParameter("opType")
        val orderId = data?.getQueryParameter("orderID")
        val token = data?.getQueryParameter("token")

        if (opType == "payment" && orderId != null && token != null) {
            captureOrder(orderId, token) { success ->
                if (success) {
                    Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Payment Capture Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (opType == "cancel") {
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show()
        }
    }


    fun captureOrder(orderId: String, token: String?,callback: (Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val client = OkHttpClient()
            if (token == null) {
                Log.e("PayPal", "Access token is null.")
                callback(false) // Inform the caller of failure
                return@launch
            }

            val url = "https://api-m.sandbox.paypal.com/v2/checkout/orders/$orderId/capture"

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post("".toRequestBody(null)) // Empty POST request
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    callback(false) // Inform the caller of failure
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("PayPalCapture", "Order $orderId captured successfully.")
                        callback(true) // Inform the caller of success
                    } else {
                        val responseBody = response.body?.string()
                        Log.e("PayPalCaptureError", "Error capturing PayPal order: ${response.message}")
                        Log.d("PayPalResponse", "Response Body: $responseBody")
                        callback(false) // Handle failure
                    }
                }
            })
        }
    }




}