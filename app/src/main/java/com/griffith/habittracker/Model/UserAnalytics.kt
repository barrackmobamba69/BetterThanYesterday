// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf

object UserAnalytics {
    private const val PREF_NAME = "analytics_prefs"
    private const val KEY_RELAPSE_COUNT = "relapse_count"
    private const val KEY_EMERGENCY_COUNT = "emergency_count"
    private const val KEY_APP_OPEN_COUNT = "app_open_count"
    private const val KEY_LAST_RELAPSE_TIME = "last_relapse_time"
    private const val KEY_LONGEST_STREAK_DAYS = "longest_streak"
    private const val KEY_LONGEST_STREAK_HOURS = "longest_streak_hours"
    private const val KEY_LONGEST_STREAK_MINUTES = "longest_streak_minutes"

    // Add a state notifier for UI updates
    val analyticsUpdateTrigger = mutableStateOf(0)

    // Add a getter method for minutes
    fun getLongestStreakMinutes(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_LONGEST_STREAK_MINUTES, 0)
    }

    fun updateLongestStreakIfNeeded(context: Context, streakStartTime: Long) {
        if (streakStartTime <= 0) return

        val currentTime = System.currentTimeMillis()
        val totalStreakDurationMillis = currentTime - streakStartTime

        // Calculate total minutes
        val totalMinutes = totalStreakDurationMillis / (1000 * 60)

        // Convert to days, hours, and minutes
        val days = (totalMinutes / (24 * 60)).toInt()
        val hours = ((totalMinutes % (24 * 60)) / 60).toInt()
        val minutes = (totalMinutes % 60).toInt()

        // Get stored longest streak
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val longestDays = prefs.getInt(KEY_LONGEST_STREAK_DAYS, 0)
        val longestHours = prefs.getInt(KEY_LONGEST_STREAK_HOURS, 0)
        val longestMinutes = prefs.getInt(KEY_LONGEST_STREAK_MINUTES, 0)

        // Convert everything to minutes for comparison
        val longestTotalMinutes = (longestDays * 24 * 60) + (longestHours * 60) + longestMinutes

        // Update if current streak is longer
        if (totalMinutes > longestTotalMinutes || longestTotalMinutes == 0) {
            Log.d("UserAnalytics", "New longest streak: $days days, $hours hours, $minutes minutes")
            prefs.edit()
                .putInt(KEY_LONGEST_STREAK_DAYS, days)
                .putInt(KEY_LONGEST_STREAK_HOURS, hours)
                .putInt(KEY_LONGEST_STREAK_MINUTES, minutes)
                .apply()

            // Trigger UI updates
            analyticsUpdateTrigger.value += 1
        }
    }

    // Increment relapse count and check if this was the longest streak
    fun recordRelapse(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_RELAPSE_COUNT, 0) + 1

        // Get current streak duration before reset
        val streakPrefs = context.getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)
        val streakStartTime = streakPrefs.getLong("streak_start_time", 0)

        // Calculate current streak duration
        if (streakStartTime > 0) {
            val currentTime = System.currentTimeMillis()
            val totalStreakDurationMillis = currentTime - streakStartTime

            // Calculate total minutes first
            val totalMinutes = totalStreakDurationMillis / (1000 * 60)

            // Convert to days, hours, and minutes
            val days = (totalMinutes / (24 * 60)).toInt()
            val hours = ((totalMinutes % (24 * 60)) / 60).toInt()
            val minutes = (totalMinutes % 60).toInt()

            // Log for debugging
            Log.d("UserAnalytics", "Current streak: $days days, $hours hours, $minutes minutes")

            // Get stored longest streak
            val longestDays = prefs.getInt(KEY_LONGEST_STREAK_DAYS, 0)
            val longestHours = prefs.getInt(KEY_LONGEST_STREAK_HOURS, 0)
            val longestMinutes = prefs.getInt(KEY_LONGEST_STREAK_MINUTES, 0)

            // Convert everything to minutes for comparison
            val longestTotalMinutes = (longestDays * 24 * 60) + (longestHours * 60) + longestMinutes

            Log.d("UserAnalytics", "Longest streak: $longestDays days, $longestHours hours, $longestMinutes minutes (total: $longestTotalMinutes minutes)")
            Log.d("UserAnalytics", "Current total minutes: $totalMinutes")

            // Update if current streak is longer
            if (totalMinutes > longestTotalMinutes || longestTotalMinutes == 0) {
                Log.d("UserAnalytics", "New longest streak: $days days, $hours hours, $minutes minutes")
                prefs.edit()
                    .putInt(KEY_LONGEST_STREAK_DAYS, days)
                    .putInt(KEY_LONGEST_STREAK_HOURS, hours)
                    .putInt(KEY_LONGEST_STREAK_MINUTES, minutes)
                    .apply()
            }
        }

        // Save relapse count and timestamp
        prefs.edit()
            .putInt(KEY_RELAPSE_COUNT, count)
            .putLong(KEY_LAST_RELAPSE_TIME, System.currentTimeMillis())
            .apply()

        // Increment our trigger to notify UI
        analyticsUpdateTrigger.value += 1
    }

    // Increment emergency button usage
    fun recordEmergency(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_EMERGENCY_COUNT, 0) + 1
        prefs.edit().putInt(KEY_EMERGENCY_COUNT, count).apply()

        // Notify UI
        analyticsUpdateTrigger.value += 1
    }

    // Increment app open count
    fun recordAppOpen(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_APP_OPEN_COUNT, 0) + 1
        prefs.edit().putInt(KEY_APP_OPEN_COUNT, count).apply()

        // Notify UI
        analyticsUpdateTrigger.value += 1
    }

    // Get relapse count
    fun getRelapseCount(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_RELAPSE_COUNT, 0)
    }

    // Get emergency count
    fun getEmergencyCount(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_EMERGENCY_COUNT, 0)
    }

    // Get app open count
    fun getAppOpenCount(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_APP_OPEN_COUNT, 0)
    }

    // Get last relapse time
    fun getLastRelapseTime(context: Context): Long {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_LAST_RELAPSE_TIME, 0)
    }

    // Get longest streak in days
    fun getLongestStreakDays(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_LONGEST_STREAK_DAYS, 0)
    }

    // Get longest streak in hours
    fun getLongestStreakHours(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_LONGEST_STREAK_HOURS, 0)
    }
}