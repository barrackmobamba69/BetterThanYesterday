// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Controller

import androidx.compose.runtime.mutableStateOf
import com.griffith.habittracker.Model.UserAnalytics
import android.content.Context

object EmergencyController {
    val showEmergencyDialog = mutableStateOf(false)

    private val motivationalQuotes = listOf(
        "You've got this! Remember why you started.",
        "Use this energy of the urge to do something productive. Clean, organize or exercise",
        "When you first began remember how desperate you were to be where you are right now",
        "You are stronger than your urges.",
        "One day at a time. You can do this.",
        "Stand up, stretch, and take a short walk. Change your surroundings to change your mindset.",
        "Either you create history or you delete history.",
        "The only limit to our realization of tomorrow will be our doubts of today.",
        "If you relapse now, it means that you care about the next 3 seconds of pleasure than the rest of your life.",
        "Refuse to be AVERAGE.",
        "Go in front of the mirror, stare yourself into the eyes, and ask WHY?",
        "You didn't come here all this way for nothing.",
        "Think what's the root cause behind this urge. How about you rather work on that. Do it with meditation.",
        "Meditate for just 5 minutes. Focus inward to find calm and clarity.",
        "Go wash your face with cold water",
        "Don't quit. Suffer now and live the rest of your life as a champion. -Muhammad Ali",
        "Are you hydrated? If not go, drink some water.",
        "Take 5 deep breaths and focus on the present moment."
    )

    fun getRandomQuote(): String {
        return motivationalQuotes.random()
    }

    fun showEmergencySupport(context: Context) {
        showEmergencyDialog.value = true

        // Added analytics tracking
        UserAnalytics.recordEmergency(context)
    }

    fun showEmergencySupport() {
        showEmergencyDialog.value = true
    }


    fun dismissEmergencySupport() {
        showEmergencyDialog.value = false
    }
}
