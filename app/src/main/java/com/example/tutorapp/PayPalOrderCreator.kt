package com.example.tutorapp
import android.util.Base64
import android.util.Log
import okhttp3.*
import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class PayPalOrderCreator {

    private val client = OkHttpClient()
    private val gson = Gson()

    // Make the function suspend to allow for asynchronous execution
    suspend fun initializeAccessToken(): String? {
        return getAccessToken(
            clientId = "AeXROYuQMXFFQ7H99Qghs07CqXiU1bnzgoc2OlPDzKB4-7J3UoughuHzQ_kysmtCRQust1tpxc2tpsv_",
            clientSecret = "EK6C_Yr6CASzUt5KsOxrPVA5UzLiAtaqwTlfsfGgfZCfHxNPmx5bEmFTe9QiJlWZwO-YAxFXVXUiB01o"
        )
    }

    // Modify getAccessToken to be a suspend function
    suspend fun getAccessToken(clientId: String, clientSecret: String): String? {
        val url = "https://api-m.sandbox.paypal.com/v1/oauth2/token"

        val credentials = "$clientId:$clientSecret"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val body = "grant_type=client_credentials"
            .toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Basic $base64Credentials")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .post(body)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = gson.fromJson(responseBody, Map::class.java)
                    json["access_token"] as? String
                } else {
                    println("Failed to retrieve access token: ${response.message}")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    // Add this function to capture the PayPal order
    fun captureOrder(orderId: String, callback: (Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val token = initializeAccessToken()
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

    // Create order function that now directly uses accessToken
    fun createOrder(amount: String, currency: String, returnUrl: String, callback: (String?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val token = initializeAccessToken() // This will now return the token asynchronously
            if (token == null) {
                Log.e("PayPal", "Access token is null.")
                callback(null)
                return@launch
            }
            Log.d("PayPal", "Access Token retrieved: $token")

            val url = "https://api-m.sandbox.paypal.com/v2/checkout/orders" // Ensure sandbox environment

            val jsonBody = """
                {
                    "intent": "CAPTURE",
                    "purchase_units": [
                        {
                            "amount": {
                                "currency_code": "$currency",
                                "value": "$amount"
                            }
                        }
                    ],
                    "application_context": {
                        "return_url": "$returnUrl"
                    }
                }
            """.trimIndent()

            Log.d("PayPalRequest", "Request Body: $jsonBody")

            val body = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token") // Use the retrieved token
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    callback(null) // Inform the caller of failure
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.d("PayPalResponse", "Response Body: $responseBody")
                        val orderResponse = gson.fromJson(responseBody, PayPalOrderResponse::class.java)
                        callback(orderResponse.id) // Pass the valid orderId
                    } else {
                        val responseBody = response.body?.string()
                        Log.e("PayPalError", "Error creating PayPal order: ${response.message}")
                        Log.d("PayPalResponse", "Response Body: $responseBody")
                        callback(null) // Handle failure
                    }
                }
            })

        }
    }

    }


// Data class to handle PayPal order response
data class PayPalOrderResponse(
    val id: String,
    val links: List<PayPalLink>
)

data class PayPalLink(
    val rel: String,
    val href: String,
    val method: String
)