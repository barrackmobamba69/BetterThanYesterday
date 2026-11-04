// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "habit_motivation"

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Motivation",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showMotivation(context: Context) {
        try {
            // Check if notifications are allowed
            if (!hasNotificationPermission(context)) {
                return // Exit if no permission
            }

            val username = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
                .getString("username", "Champion") ?: "Champion"

            val messages = listOf(
                "$username, you've got this! 💪",
                "Stay strong $username! 🔥",
                "Keep going $username! ✨",
                "You're going amazing $username! 🌟"
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Stay Motivated!")
                .setContentText(messages.random())
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(1, notification)
        } catch (e: SecurityException) {
            // Handle permission denied
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires explicit permission
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Pre-Android 13 - check if notifications are enabled
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
}