// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

//Manages sound playback logic
package com.griffith.habittracker.Controller

import android.media.MediaPlayer
import android.content.Context
import com.griffith.habittracker.Model.MeditationSession
import com.griffith.habittracker.R

class MeditationController(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun getSoundOptions(): List<String> {
        return listOf(
            "No Sound", // Users can meditate in silence
            "White Noise", "Brown Noise", "Pink Noise", "Wim Hof",
            "Handpan Music", "528 hz Singing Bowl", "Rainfall", "Ocean Waves"
        )
    }

    // Function to play the selected sound
    fun playSound(soundType: String?) {
        stopSound()

        if (soundType == null || soundType == "No Sound") return

        // Determine the corresponding sound resource ID based on the selected sound
        val soundResourceId = when(soundType) {
            "White Noise" -> R.raw.white_noise
            "Brown Noise" -> R.raw.brown_noise
            "Pink Noise" -> R.raw.pink_noise
            "528 hz Singing Bowl" -> R.raw.hz_528_singing_bowl
            "Wim Hof" -> R.raw.guided_wim_hof_method_breathing
            "Handpan Music" -> R.raw.handpan_music_by_malte_marten
            "Ocean Waves" -> R.raw.ocean_waves
            "Rainfall" -> R.raw.rain_sounds
            else -> return
        }

        mediaPlayer = MediaPlayer.create(context, soundResourceId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                release()
            }
        }
        mediaPlayer = null
    }
}
