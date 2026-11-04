// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.Controller

import android.content.Context
import com.griffith.habittracker.Model.Task
import com.griffith.habittracker.Model.TaskDayStats
import com.griffith.habittracker.Model.TaskOverallStats
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object TaskController {
    private const val PREF_NAME = "task_prefs"
    private const val KEY_TASKS = "tasks"
    private const val STATS_PREF_NAME = "task_stats"

    // Default tasks that everyone gets
    private val defaultTasks = listOf(
        Task("workout", "Exercise for 1 hour", false, true),
        Task("meditate", "Meditate for 10-20 minutes", false, true),
        Task("hydrate", "Drink 2L-3L of water", false, true),
        Task("journal", "Write in your journal", false, true)
    )

    // Get all tasks (default + custom)
    fun getTasks(context: Context): List<Task> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val tasksJson = prefs.getString(KEY_TASKS, "[]")
        val savedTasks = parseTasksFromJson(tasksJson!!)

        // Make sure default tasks are always included
        val result = savedTasks.toMutableList()
        for (defaultTask in defaultTasks) {
            if (result.none { it.id == defaultTask.id }) {
                result.add(defaultTask)
            }
        }
        return result
    }

    // Toggle a task's completion status
    fun toggleTask(context: Context, taskId: String) {
        val tasks = getTasks(context).toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            val task = tasks[index]
            tasks[index] = task.copy(isCompleted = !task.isCompleted)
            saveTasks(context, tasks)

            // Record daily stats when tasks change
            recordDailyStats(context)
        }
    }

    // Add a new custom task
    fun addTask(context: Context, title: String) {
        val tasks = getTasks(context).toMutableList()
        tasks.add(Task(title = title))
        saveTasks(context, tasks)

        // Update stats when adding task
        recordDailyStats(context)
    }

    // Save tasks to SharedPreferences
    private fun saveTasks(context: Context, tasks: List<Task>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = tasksToJson(tasks)
        prefs.edit().putString(KEY_TASKS, json).apply()
    }

    // Reset tasks for a new day
    fun resetDailyTasks(context: Context) {
        val tasks = getTasks(context).map { it.copy(isCompleted = false) }
        saveTasks(context, tasks)
        recordDailyStats(context)
    }

    // Delete task
    fun deleteTask(context: Context, taskId: String) {
        val tasks = getTasks(context).toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            // Don't delete default tasks
            if (!tasks[index].isDefault) {
                tasks.removeAt(index)
                saveTasks(context, tasks)
                recordDailyStats(context)
            }
        }
    }

    // Rename task
    fun renameTask(context: Context, taskId: String, newTitle: String) {
        val tasks = getTasks(context).toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            val task = tasks[index]
            tasks[index] = task.copy(title = newTitle)
            saveTasks(context, tasks)
        }
    }


    // ============ TASK TRACKING FUNCTIONS ============

    // Record daily task completion stats
    fun recordDailyStats(context: Context) {
        val tasks = getTasks(context)
        val completedTasks = tasks.count { it.isCompleted }
        val totalTasks = tasks.size

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val prefs = context.getSharedPreferences(STATS_PREF_NAME, Context.MODE_PRIVATE)

        prefs.edit()
            .putInt("${today}_total", totalTasks)
            .putInt("${today}_completed", completedTasks)
            .apply()
    }

    // Get task completion stats for last 7 days
    fun getTaskCompletionStats(context: Context): List<TaskDayStats> {
        val stats = mutableListOf<TaskDayStats>()
        val prefs = context.getSharedPreferences(STATS_PREF_NAME, Context.MODE_PRIVATE)

        // Get last 7 days
        for (i in 6 downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val totalTasks = prefs.getInt("${dateKey}_total", 0)
            val completedTasks = prefs.getInt("${dateKey}_completed", 0)

            stats.add(TaskDayStats(
                date = dateKey,
                totalTasks = totalTasks,
                completedTasks = completedTasks,
                completionRate = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0
            ))
        }

        return stats
    }

    // Get overall task statistics for today
    fun getOverallTaskStats(context: Context): TaskOverallStats {
        val tasks = getTasks(context)
        val completedToday = tasks.count { it.isCompleted }
        val totalTasks = tasks.size
        val completionRate = if (totalTasks > 0) (completedToday * 100) / totalTasks else 0

        return TaskOverallStats(
            totalTasks = totalTasks,
            completedToday = completedToday,
            completionRate = completionRate
        )
    }

    // ============ HELPER FUNCTIONS ============

    // Helper function to convert tasks to JSON
    private fun tasksToJson(tasks: List<Task>): String {
        val jsonArray = JSONArray()
        for (task in tasks) {
            val jsonObject = JSONObject()
            jsonObject.put("id", task.id)
            jsonObject.put("title", task.title)
            jsonObject.put("isCompleted", task.isCompleted)
            jsonObject.put("isDefault", task.isDefault)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    // Helper function to parse JSON back to tasks
    private fun parseTasksFromJson(json: String): List<Task> {
        val result = mutableListOf<Task>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val task = Task(
                    id = jsonObject.getString("id"),
                    title = jsonObject.getString("title"),
                    isCompleted = jsonObject.getBoolean("isCompleted"),
                    isDefault = jsonObject.getBoolean("isDefault")
                )
                result.add(task)
            }
        } catch (e: Exception) {
            // Return empty list if there's a parsing error
        }
        return result
    }
}