// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Controller

import android.content.Context
import com.griffith.habittracker.Model.UserAnalytics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AnalyticsController {
    // Format the last relapse date
    fun getLastRelapseFormatted(context: Context): String {
        val timestamp = UserAnalytics.getLastRelapseTime(context)
        if (timestamp == 0L) return "Never"

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    // Initialize analytics tracking
    fun initializeAnalytics(context: Context) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        if (!prefs.contains("first_use_time")) {
            prefs.edit().putLong("first_use_time", System.currentTimeMillis()).apply()
        }
    }
}