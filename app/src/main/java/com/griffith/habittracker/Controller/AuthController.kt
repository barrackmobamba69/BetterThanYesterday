// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Controller

import android.content.Context
import java.security.MessageDigest

object AuthController {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_PASSWORD = "user_password"
    private const val KEY_NAME = "user_name"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    // Hash password for security (converts password to unreadable hash)
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    // Register new user
    fun register(context: Context, email: String, password: String, name: String): Boolean {
        if (email.isBlank() || password.isBlank() || name.isBlank()) return false
        if (password.length < 6) return false

        // Hash password for security
        val hashedPassword = hashPassword(password)

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, hashedPassword) // Store hashed password
            .putString(KEY_NAME, name)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()

        // Also save to settings for consistency
        val settingsPrefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        settingsPrefs.edit()
            .putString("username", name) // Use full name as initial username
            .apply()

        return true
    }

    // Login user
    fun login(context: Context, email: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedEmail = prefs.getString(KEY_EMAIL, "")
        val savedPasswordHash = prefs.getString(KEY_PASSWORD, "")

        // Hash input password to compare with stored hash
        val inputPasswordHash = hashPassword(password)

        if (email == savedEmail && inputPasswordHash == savedPasswordHash) {
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()

            // Sync username to settings when logging in
            val savedName = prefs.getString(KEY_NAME, "")
            if (!savedName.isNullOrEmpty()) {
                val settingsPrefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
                val currentUsername = settingsPrefs.getString("username", "")
                if (currentUsername.isNullOrEmpty()) {
                    settingsPrefs.edit()
                        .putString("username", savedName)
                        .apply()
                }
            }

            return true
        }
        return false
    }

    // Check if user is logged in
    fun isLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Logout user
    fun logout(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()

        // Clear settings username on logout
        context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
            .edit()
            .remove("username")
            .apply()
    }

    // Get user name
    fun getUserName(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_NAME, "User") ?: "User"
    }

    // Get user email
    fun getUserEmail(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "") ?: ""
    }

    // Validate email format
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    // Enhanced register with better validation
    fun registerWithValidation(context: Context, email: String, password: String, name: String): RegisterResult {
        // Validate email format
        if (!isValidEmail(email)) {
            return RegisterResult.INVALID_EMAIL
        }

        // Validate password strength
        if (password.length < 6) {
            return RegisterResult.WEAK_PASSWORD
        }

        // Validate name
        if (name.trim().isEmpty()) {
            return RegisterResult.INVALID_NAME
        }

        // Check if user already exists
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existingEmail = prefs.getString(KEY_EMAIL, "")
        if (existingEmail == email) {
            return RegisterResult.USER_EXISTS
        }

        // Register user
        return if (register(context, email, password, name.trim())) {
            RegisterResult.SUCCESS
        } else {
            RegisterResult.UNKNOWN_ERROR
        }
    }

    // Result enum for better error handling
    enum class RegisterResult {
        SUCCESS,
        INVALID_EMAIL,
        WEAK_PASSWORD,
        INVALID_NAME,
        USER_EXISTS,
        UNKNOWN_ERROR
    }
}