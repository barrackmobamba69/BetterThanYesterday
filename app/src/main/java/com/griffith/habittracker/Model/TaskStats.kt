// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Model

data class TaskDayStats(
    val date: String,
    val totalTasks: Int,
    val completedTasks: Int,
    val completionRate: Int
)

data class TaskOverallStats(
    val totalTasks: Int,
    val completedToday: Int,
    val completionRate: Int
)
