// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.View

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.griffith.habittracker.Controller.TaskController
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun StatsScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Get the ViewModel
    val viewModel: StatsViewModel = viewModel()

    // Collect state from ViewModel
    val longestDays by viewModel.longestStreakDays.collectAsState()
    val longestHours by viewModel.longestStreakHours.collectAsState()
    val longestMinutes by viewModel.longestStreakMinutes.collectAsState()
    val relapseCount by viewModel.relapseCount.collectAsState()
    val emergencyCount by viewModel.emergencyCount.collectAsState()
    val appOpenCount by viewModel.appOpenCount.collectAsState()
    val lastRelapseTime by viewModel.lastRelapseTime.collectAsState()

    // State to track if charts are expanded
    var userStatsExpanded by remember { mutableStateOf(false) }
    var taskStatsExpanded by remember { mutableStateOf(false) }

    // Load data when screen is first shown and check for updates
    LaunchedEffect(Unit) {
        viewModel.loadData(context)
    }

    // Check for updates periodically
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.checkForUpdates(context)
            kotlinx.coroutines.delay(1000) // Check every second
        }
    }

    NavigationDrawer(
        navController = navController,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // User Statistics Card - CLICKABLE
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { userStatsExpanded = !userStatsExpanded }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "User Statistics",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Arrow icon to expand card
                            Icon(
                                imageVector = if (userStatsExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (userStatsExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Relapse count
                            StatBox(
                                title = "Relapses",
                                value = "$relapseCount",
                                modifier = Modifier.weight(1f)
                            )

                            // Emergency count
                            StatBox(
                                title = "Emergency",
                                value = "$emergencyCount",
                                modifier = Modifier.weight(1f)
                            )

                            // App opens
                            StatBox(
                                title = "App Opens",
                                value = "$appOpenCount",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Expanded chart
                        if (userStatsExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Chart component
                            Chart(
                                chart = columnChart(),
                                model = entryModelOf(relapseCount.toFloat(), emergencyCount.toFloat(), appOpenCount.toFloat()),
                                startAxis = startAxis(),
                                bottomAxis = bottomAxis(
                                    valueFormatter = { value, _ ->
                                        when (value.toInt()) {
                                            0 -> "Relapses"
                                            1 -> "Emergency"
                                            2 -> "App Opens"
                                            else -> ""
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }

                // NEW: Task Completion Statistics Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { taskStatsExpanded = !taskStatsExpanded }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Task Completion",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Icon(
                                imageVector = if (taskStatsExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (taskStatsExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Today's task stats
                        val taskStats = remember { TaskController.getOverallTaskStats(context) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox(
                                title = "Total Tasks",
                                value = "${taskStats.totalTasks}",
                                modifier = Modifier.weight(1f)
                            )

                            StatBox(
                                title = "Completed",
                                value = "${taskStats.completedToday}",
                                modifier = Modifier.weight(1f)
                            )

                            StatBox(
                                title = "Completed",
                                value = "${taskStats.completionRate}%",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Expanded 7-day chart
                        if (taskStatsExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "7-Day Progress",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TaskCompletionChart(context = context)
                        }
                    }
                }

                // Last Relapse Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Last Relapse",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lastRelapseTime,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Longest Streak Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Highest Streak in DD:HH:MM",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (longestDays > 0 || longestHours > 0 || longestMinutes > 0) {
                                when {
                                    longestDays > 0 -> {
                                        "$longestDays days, $longestHours hours, $longestMinutes minutes"
                                    }
                                    longestHours > 0 -> {
                                        "$longestHours hours, $longestMinutes minutes"
                                    }
                                    else -> {
                                        "$longestMinutes minutes"
                                    }
                                }
                            } else {
                                "No data found yet!"
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


// Vico Charts Implementation
@Composable
fun TaskCompletionChart(context: Context) {
    val taskStats = remember { TaskController.getTaskCompletionStats(context) }

    if (taskStats.isNotEmpty() && taskStats.any { it.totalTasks > 0 }) {
        // Chart with completion rate
        Chart(
            chart = columnChart(), // Creates a bar chart
            model = entryModelOf(
                *taskStats.map { it.completionRate.toFloat() }.toTypedArray()
            ),
            startAxis = startAxis(
                title = "Completion %"
            ),
            bottomAxis = bottomAxis(
                valueFormatter = { value, _ ->
                    val index = value.toInt()
                    if (index >= 0 && index < taskStats.size) {
                        val date = taskStats[index].date
                        try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                            val parsedDate = inputFormat.parse(date)
                            outputFormat.format(parsedDate ?: Date())
                        } catch (e: Exception) {
                            "N/A"
                        }
                    } else ""
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    } else {
        Text(
            text = "Complete some tasks to see your progress chart! 📊",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}