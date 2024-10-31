package com.example.tutorapp

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private const val PREF_NAME = "UserPrefs"
    private const val USER_ID_KEY = "userID"

    // Initialize the SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private lateinit var sharedPreferences: SharedPreferences

    // Function to save the logged-in user ID
    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
    }

    // Function to get the logged-in user ID
    fun getLoggedInUserId(): String? {
        return sharedPreferences.getString(USER_ID_KEY, null)
    }

    // Function to clear user data (e.g., on logout)
    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}
