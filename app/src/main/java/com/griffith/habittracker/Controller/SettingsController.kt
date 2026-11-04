// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Controller

import android.content.Context
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object SettingsController {
    private const val PREF_NAME = "settings_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_TEXT_SIZE = "text_size"
    private const val KEY_THEME_COLOR = "theme_color"
    private const val KEY_NOTIFICATIONS = "notifications"

    // Username
    fun setUsername(context: Context, username: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USERNAME, username)
            .apply()
    }

    fun getUsername(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USERNAME, "User") ?: "User"
    }

    // Theme Mode (Dark/Light)
    fun setDarkMode(context: Context, isDarkMode: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, isDarkMode)
            .apply()
    }

    fun isDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)
    }

    // Text Size
    fun setTextSize(context: Context, size: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_TEXT_SIZE, size)
            .apply()
    }

    fun getTextSize(context: Context): TextUnit {
        val size = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_TEXT_SIZE, 16)
        return size.sp
    }

    // Theme Color
    fun setThemeColor(context: Context, colorName: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME_COLOR, colorName)
            .apply()
    }

    fun getThemeColor(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME_COLOR, "Default") ?: "Default"
    }

    // Notifications
    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_NOTIFICATIONS, enabled)
            .apply()
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_NOTIFICATIONS, true)
    }

    // Reset all app data
    fun resetAppData(context: Context) {
        // Clear UserPreferences
        val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userPrefs.edit().clear().apply()

        // Clear streak data
        val streakPrefs = context.getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)
        streakPrefs.edit().clear().apply()

        // Clear tasks
        val taskPrefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        taskPrefs.edit().clear().apply()

        // Preserve settings
    }
}