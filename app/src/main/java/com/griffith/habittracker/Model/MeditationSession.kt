// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

//Stores the meditation session details
package com.griffith.habittracker.Model

data class MeditationSession(
    val soundType: String? = null, // No sound by default
    val duration: Int = 0,  // Duration in minutes
    val volumeLevel: Float = 1.0f,  // Volume level
    val timestamp: Long = System.currentTimeMillis() // Session start time
)