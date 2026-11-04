package com.griffith.habittracker

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.griffith.habittracker.Controller.AnalyticsController
import com.griffith.habittracker.Controller.NotificationHelper
import com.griffith.habittracker.Model.UserAnalytics
import com.griffith.habittracker.Model.UserPreferences
import com.griffith.habittracker.View.OnboardingNavigation
import com.griffith.habittracker.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    // Request notification permission
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notification channel
        NotificationHelper.createChannel(this)

        // Checks if phone is Android 13 or newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Initialize analytics and record app open timestamps
        AnalyticsController.initializeAnalytics(this)
        UserAnalytics.recordAppOpen(this)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        OnboardingNavigation()
                    }
                }
            }
        }
    }

    // Show notification when app goes to background/closes
    override fun onStop() {
        super.onStop()
        NotificationHelper.showMotivation(this)
    }
}
