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
import java.text.SimpleDateFormat
import java.util.*
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

class StudentEnroll : AppCompatActivity() {
    private var tutorid = ""
    private val orderCreator = PayPalOrderCreator()
    private lateinit var selectedTutor: TutorAvailability
    private lateinit var tableLayout: TableLayout
    private val database = FirebaseDatabase.getInstance()
    private val availabilityRef = database.reference.child("tutor_availabilities")
    private val enrollmentsRef: DatabaseReference = database.reference.child("student_tutor_enrollments")
    private lateinit var currentUserId: String  // To hold the current student's ID
    private lateinit var currentUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutors_found)

        // Initialize SharedPreferences
        UserPreferences.init(this)

        // Get the selected course and date from the Intent
        val selectedCourse = intent.getStringExtra("selectedCourse")
        val selectedDate = intent.getLongExtra("selectedDate", 0L)
        val selectedGrade = intent.getStringExtra("selectedGrade")
        val fullName = intent.getStringExtra("fullName")
        currentUserName = fullName?:""
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
            getTutors(selectedCourse, selectedDate,selectedGrade?:"")
        }
    }

    private fun getTutors(course: String, date: Long, grade: String) {
        availabilityRef.orderByChild("course").equalTo(course).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tableLayout.removeAllViews() // Clear previous results

                if (snapshot.exists()) {
                    var tutorFound = false
                    for (tutorSnapshot in snapshot.children) {
                        val tutorAvailability = tutorSnapshot.getValue(TutorAvailability::class.java)
                        if (tutorAvailability != null && tutorAvailability.date == date && tutorAvailability.gradeLevel.toInt() >= grade.toInt()) {
                            addTutorToTable(tutorAvailability,tutorSnapshot.key!!)
                            tutorFound = true
                        }
                    }


                } else {
                    Toast.makeText(this@StudentEnroll, "No tutors available for this course.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StudentEnroll, "Error fetching tutors", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTutorToTable(tutor: TutorAvailability,tutorId: String) {
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

            tutorid = tutorId
            selectedTutor = tutor
            startPayPalPayment("40.00","CAD")
        }

        // Add all the views to the row
        row.addView(tutorName)
        row.addView(certificates)
        row.addView(timeSlot)
        row.addView(enrollButton)

        // Add the row to the TableLayout
        tableLayout.addView(row)
    }

    private fun startPayPalPayment(amount: String, currency: String) {

        val paypal_client_id = "AeXROYuQMXFFQ7H99Qghs07CqXiU1bnzgoc2OlPDzKB4-7J3UoughuHzQ_kysmtCRQust1tpxc2tpsv_"
        val coreConfig = CoreConfig(paypal_client_id, Environment.SANDBOX)
        val payPalWebCheckoutClient =
            PayPalWebCheckoutClient(this@StudentEnroll, coreConfig, "com.example.tutorapp://paypal")





        orderCreator.createOrder(amount, currency) { orderId ->
            if (orderId != null && orderId.isNotEmpty()) {
                val checkoutRequest = PayPalWebCheckoutRequest(orderId, fundingSource = PayPalWebCheckoutFundingSource.PAYPAL)
                Log.d("PayPal", "Order ID: $orderId")
                // Set the listener to handle the result of the checkout process




                payPalWebCheckoutClient.start(
                    activity = this,
                    request = checkoutRequest
                )


            } else {
                println("Failed to create PayPal order.")
            }

        }

    }

    private fun enrollStudentWithTutor(tutor: TutorAvailability,tutorId: String) {
        if (currentUserId.isNotEmpty()) {
            // Create a new enrollment entry in the database
            val enrollmentId = enrollmentsRef.push().key
            val enrollment = mapOf(
                "studentId" to currentUserId,
                "tutorName" to tutor.name,  // Assuming you have tutor's ID in the TutorAvailability object
                "course" to tutor.course,
                "date" to tutor.date,
                "timeSlot" to tutor.timeSlot,
                "enrollmentId" to enrollmentId.toString(),
                "tutorId" to tutor.tutorId,
                "studentName" to currentUserName
            )

            // Add the enrollment to the "student_tutor_enrollments" node
            enrollmentsRef.child(enrollmentId!!).setValue(enrollment)
                .addOnSuccessListener {
                    removeTutorAvailability(tutorId)
                    Toast.makeText(this, "You have successfully enrolled with ${tutor.name}!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Enrollment failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "You need to log in to enroll.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeTutorAvailability(tutorId: String) {
        // Remove the tutor's availability from the "tutor_availabilities" node
        availabilityRef.child(tutorId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Tutor availability removed.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove tutor availability.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }
    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(newIntent)

        intent = newIntent


        val data = intent?.data

        val opType = data?.getQueryParameter("opType")
        val token = data?.getQueryParameter("token")
        Log.d("PayPal", "opType: $opType,  token: $token")
        Log.d("PayPal", "selectedTutor: $selectedTutor!!")


        if (token != null) {
            orderCreator.captureOrder(token) { success ->}
            enrollStudentWithTutor(selectedTutor, tutorid)

        }
    }
}


