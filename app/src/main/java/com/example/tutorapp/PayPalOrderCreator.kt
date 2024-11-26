package com.example.tutorapp
import okhttp3.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class PayPalOrderCreator {

    private val client = OkHttpClient()
    private val gson = Gson()


    private val accessToken = "AeXROYuQMXFFQ7H99Qghs07CqXiU1bnzgoc2OlPDzKB4-7J3UoughuHzQ_kysmtCRQust1tpxc2tpsv_:EK6C_Yr6CASzUt5KsOxrPVA5UzLiAtaqwTlfsfGgfZCfHxNPmx5bEmFTe9QiJlWZwO-YAxFXVXUiB01o"

    // Create order function
    fun createOrder(amount: String, currency: String, returnUrl: String) {
        val url = "https://api-m.paypal.com/v2/checkout/orders"

        // Prepare request body with JSON
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
                    "return_url": "$returnUrl",
                    
                }
            }
        """.trimIndent()

        val body = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        // Create the request
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")  // Add your access token here
            .post(body)
            .build()

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Parse the response to get order details
                    val responseBody = response.body?.string()
                    val orderResponse = gson.fromJson(responseBody, PayPalOrderResponse::class.java)
                    val approvalUrl = getApprovalUrl(orderResponse)

                    // Proceed to the PayPal approval page using the approval URL
                    println("Approval URL: $approvalUrl")
                    // You would redirect the user to this URL for payment approval
                } else {
                    // Handle error response
                    println("Error creating order: ${response.message}")
                }
            }
        })
    }

    // Extract approval URL from PayPal order response
    private fun getApprovalUrl(orderResponse: PayPalOrderResponse): String? {
        return orderResponse.links?.firstOrNull { it.rel == "approve" }?.href
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
