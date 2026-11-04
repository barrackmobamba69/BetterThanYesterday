// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Model

import android.content.Context

object UserPreferences {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_HABIT = "habit"
    private const val KEY_REASON = "reason"

    fun isOnboardingCompleted(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun saveUserDetails(context: Context, habit: String, reason: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_HABIT, habit)
            .putString(KEY_REASON, reason)
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }
}