package com.example.newapp.utils

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class EmailManager {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val apiKey = "REPLACE_WITH_YOUR_API_KEY"
    private val fromEmail = "onboarding@resend.dev" // Default for Resend testing, user provided gowri@... might not work without domain verification

    fun sendEmail(to: String, subject: String, content: String, onResult: (Boolean) -> Unit) {
        val url = "https://api.resend.com/emails"
        
        val bodyMap = mapOf(
            "from" to fromEmail,
            "to" to listOf(to),
            "subject" to subject,
            "html" to content
        )
        
        val json = gson.toJson(bodyMap)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(false)
            }

            override fun onResponse(call: Call, response: Response) {
                onResult(response.isSuccessful)
                response.close()
            }
        })
    }
}
