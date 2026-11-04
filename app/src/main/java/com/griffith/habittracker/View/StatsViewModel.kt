package com.griffith.habittracker.View

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.habittracker.Controller.StreakController
import com.griffith.habittracker.Model.UserAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel : ViewModel() {
    // State for streak data
    private val _longestStreakDays = MutableStateFlow(0)
    val longestStreakDays: StateFlow<Int> = _longestStreakDays.asStateFlow()

    private val _longestStreakHours = MutableStateFlow(0)
    val longestStreakHours: StateFlow<Int> = _longestStreakHours.asStateFlow()

    private val _longestStreakMinutes = MutableStateFlow(0)
    val longestStreakMinutes: StateFlow<Int> = _longestStreakMinutes.asStateFlow()

    // State for analytics data
    private val _relapseCount = MutableStateFlow(0)
    val relapseCount: StateFlow<Int> = _relapseCount.asStateFlow()

    private val _emergencyCount = MutableStateFlow(0)
    val emergencyCount: StateFlow<Int> = _emergencyCount.asStateFlow()

    private val _appOpenCount = MutableStateFlow(0)
    val appOpenCount: StateFlow<Int> = _appOpenCount.asStateFlow()

    // Last relapse time
    private val _lastRelapseTime = MutableStateFlow("")
    val lastRelapseTime: StateFlow<String> = _lastRelapseTime.asStateFlow()

    // Initialize the observers for streak and analytics updates
    private var streakUpdateObserver: Int = StreakController.stateUpdateTrigger.value
    private var analyticsUpdateObserver: Int = UserAnalytics.analyticsUpdateTrigger.value

    init {
        // When ViewModel is created, force it to load data
        streakUpdateObserver = StreakController.stateUpdateTrigger.value - 1
        analyticsUpdateObserver = UserAnalytics.analyticsUpdateTrigger.value - 1
    }

    // Load data from context
    fun loadData(context: Context) {
        viewModelScope.launch {
            updateStreakData(context)
            updateAnalyticsData(context)
        }
    }

    // Update streak data
    fun updateStreakData(context: Context) {
        _longestStreakDays.value = UserAnalytics.getLongestStreakDays(context)
        _longestStreakHours.value = UserAnalytics.getLongestStreakHours(context)
        _longestStreakMinutes.value = UserAnalytics.getLongestStreakMinutes(context)
    }

    // Update analytics data
    fun updateAnalyticsData(context: Context) {
        _relapseCount.value = UserAnalytics.getRelapseCount(context)
        _emergencyCount.value = UserAnalytics.getEmergencyCount(context)
        _appOpenCount.value = UserAnalytics.getAppOpenCount(context)

        // Get last relapse time
        val lastRelapseTimestamp = UserAnalytics.getLastRelapseTime(context)
        _lastRelapseTime.value = formatLastRelapseTime(lastRelapseTimestamp)
    }

    // Format last relapse time
    private fun formatLastRelapseTime(timestamp: Long): String {
        if (timestamp == 0L) return "Never"

        val date = java.util.Date(timestamp)
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
        return sdf.format(date)
    }

    // Record a relapse and update stats
    fun handleRelapse(context: Context) {
        UserAnalytics.recordRelapse(context)
        // Update both streak and analytics data
        updateStreakData(context)
        updateAnalyticsData(context)
    }

    // Check if the streak update trigger or analytics update trigger has changed and update if needed
    fun checkForUpdates(context: Context) {
        val currentStreakTrigger = StreakController.stateUpdateTrigger.value
        val currentAnalyticsTrigger = UserAnalytics.analyticsUpdateTrigger.value

        if (currentStreakTrigger != streakUpdateObserver ||
            currentAnalyticsTrigger != analyticsUpdateObserver) {

            // Update both observer values
            streakUpdateObserver = currentStreakTrigger
            analyticsUpdateObserver = currentAnalyticsTrigger

            // Reload all data
            loadData(context)
        }
    }
}