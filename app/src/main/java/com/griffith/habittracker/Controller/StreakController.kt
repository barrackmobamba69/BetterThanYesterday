// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Controller

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.griffith.habittracker.Model.UserAnalytics
import java.util.concurrent.TimeUnit

object StreakController {

    val stateUpdateTrigger = mutableStateOf(0)

    // Using a singleton object to maintain streak across screens
    val streakStartTime = mutableStateOf<Long?>(null)

    // Time components
    val days = mutableStateOf(0)
    val hours = mutableStateOf(0)
    val minutes = mutableStateOf(0)
    val seconds = mutableStateOf(0)

    // Load saved streak time
    fun loadStreak(context: Context) {
        val prefs = context.getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)
        val savedTime = prefs.getLong("streak_start_time", 0)
        if (savedTime > 0) {
            streakStartTime.value = savedTime
            updateTimeComponents()
        }
    }

    // Start the streak timer
    fun startStreak(context: Context) {
        streakStartTime.value = System.currentTimeMillis()
        // Save to preferences
        val prefs = context.getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("streak_start_time", streakStartTime.value!!).apply()
        updateTimeComponents()
    }

    // Record a relapse and restart timer
    fun recordRelapse(context: Context) {
        // Save the old streak start time before resetting
        val prefs = context.getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)
        val oldStreakStartTime = prefs.getLong("streak_start_time", 0)

        // Reset and restart
        streakStartTime.value = System.currentTimeMillis()

        // Save to preferences
        prefs.edit().putLong("streak_start_time", streakStartTime.value!!).commit()
        updateTimeComponents()

        // CRUCIAL CHANGE: Call the new helper method to update longest streak directly
        if (oldStreakStartTime > 0) {
            UserAnalytics.updateLongestStreakIfNeeded(context, oldStreakStartTime)
        }

        // Adding the analytics tracking
        UserAnalytics.recordRelapse(context)

        // Increment the trigger to notify observers
        stateUpdateTrigger.value += 1
    }

    // Calculate and update time components
    fun updateTimeComponents() {
        if (streakStartTime.value == null) {
            days.value = 0
            hours.value = 0
            minutes.value = 0
            seconds.value = 0
            return
        }

        val currentTime = System.currentTimeMillis()
        val elapsedMillis = currentTime - streakStartTime.value!!

        // Convert to days, hours, minutes, seconds
        days.value = TimeUnit.MILLISECONDS.toDays(elapsedMillis).toInt()
        val remainingHours = elapsedMillis - TimeUnit.DAYS.toMillis(days.value.toLong())
        hours.value = TimeUnit.MILLISECONDS.toHours(remainingHours).toInt()
        val remainingMinutes = remainingHours - TimeUnit.HOURS.toMillis(hours.value.toLong())
        minutes.value = TimeUnit.MILLISECONDS.toMinutes(remainingMinutes).toInt()
        val remainingSeconds = remainingMinutes - TimeUnit.MINUTES.toMillis(minutes.value.toLong())
        seconds.value = TimeUnit.MILLISECONDS.toSeconds(remainingSeconds).toInt()
    }
}