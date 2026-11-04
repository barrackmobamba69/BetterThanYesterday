// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.View

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.griffith.habittracker.Controller.EmergencyController
import com.griffith.habittracker.Controller.StreakController
import com.griffith.habittracker.Controller.YouTubeController
import com.griffith.habittracker.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    //val scrollState = rememberScrollState()

    // State to control whether to show YouTube dialog
    var showYouTubeDialog by remember { mutableStateOf(false) }

    // Update time components every second
    LaunchedEffect(Unit) {
        while (true) {
            StreakController.updateTimeComponents()
            delay(1000) // Update every second
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
                            text = "Dashboard",
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
                    actions = {
                        // Emergency button
                        Button(
                            onClick = { EmergencyController.showEmergencySupport(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Emergency")
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
                    //.verticalScroll(scrollState),
                //horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Streak Counter Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Time Since Last Relapse",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Time display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Days
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${StreakController.days.value}",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("DAYS")
                            }

                            // Hours
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${StreakController.hours.value}",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("HRS")
                            }

                            // Minutes
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${StreakController.minutes.value}",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("MINS")
                            }

                            // Seconds
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${StreakController.seconds.value}",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("SECS")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Relapse Button
                Button(
                    onClick = {
                        // First record the relapse
                        StreakController.recordRelapse(context)

                        // Then force the UserAnalytics to update longest streak values
                        // Get current streak duration that just ended
                        // Calculate how long was the streak
                        val streakPrefs = context.getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)
                        val streakStartTime = streakPrefs.getLong("streak_start_time", 0)

                        if (streakStartTime > 0) {
                            val currentTime = System.currentTimeMillis()
                            val totalStreakDurationMillis = currentTime - streakStartTime

                            // Calculate total minutes
                            val totalMinutes = totalStreakDurationMillis / (1000 * 60)

                            // Convert to days, hours, and minutes
                            val days = (totalMinutes / (24 * 60)).toInt()
                            val hours = ((totalMinutes % (24 * 60)) / 60).toInt()
                            val minutes = (totalMinutes % 60).toInt()

                            // Get stored longest streak from preferences
                            val analyticsPrefs = context.getSharedPreferences("analytics_prefs", Context.MODE_PRIVATE)
                            val longestDays = analyticsPrefs.getInt("longest_streak", 0)
                            val longestHours = analyticsPrefs.getInt("longest_streak_hours", 0)
                            val longestMinutes = analyticsPrefs.getInt("longest_streak_minutes", 0)

                            // Convert everything to minutes for comparison
                            val longestTotalMinutes = (longestDays * 24 * 60) + (longestHours * 60) + longestMinutes

                            // Update if current streak is longer
                            if (totalMinutes > longestTotalMinutes || longestTotalMinutes == 0) {
                                // Save new longest streak to preferences
                                analyticsPrefs.edit()
                                    .putInt("longest_streak", days)
                                    .putInt("longest_streak_hours", hours)
                                    .putInt("longest_streak_minutes", minutes)
                                    .apply()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Relapse",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Tasks Section with height constraint
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 290.dp)
                ) {
                    TaskSection()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to show YouTube videos dialog
                Button(
                    onClick = { showYouTubeDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Watch Videos",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Watch YouTube Videos",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Emergency Support Dialog
    if (EmergencyController.showEmergencyDialog.value) {
        val quote = remember { EmergencyController.getRandomQuote() }

        AlertDialog(
            onDismissRequest = { EmergencyController.dismissEmergencySupport() },
            title = {
                Text(
                    "Think about it!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        quote,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { EmergencyController.dismissEmergencySupport() }
                ) {
                    Text("I Feel Better")
                }
            }
        )
    }

    // YouTube Videos Dialog - Only shown when button is clicked
    if (showYouTubeDialog) {
        Dialog(
            onDismissRequest = { showYouTubeDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // YouTube content with scrolling
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header with title and close button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YouTube Videos",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        IconButton(
                            onClick = { showYouTubeDialog = false }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Youtube section
                    YouTubeVideosList()
                }
            }
        }
    }
}

@Composable
fun YouTubeVideosList() {
    val context = LocalContext.current
    val videos = YouTubeController.getVideos()

    videos.forEach { video ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    YouTubeController.openYoutubeVideo(video.title, context)
                }
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // Try to load image from resource
                Image(
                    painter = painterResource(
                        id = when (video.title) {
                            "The Simplest Daily Routine for Self-Improvement" ->
                                R.drawable.the_simplest_daily_routine_for_self_improvement

                            "This morning routine is scientifically proven to make you limitless" ->
                                R.drawable.this_morning_routine_is_scientifically_proven_to_make_you_limitless

                            "How to learn anything faster than everyone" ->
                                R.drawable.how_to_learn_anything_faster_than_everyone

                            "Feeling lost in your twenties" ->
                                R.drawable.feeling_lost_in_your_twenties

                            "You waste too much time and it needs to stop" ->
                                R.drawable.you_waste_too_much_time_and_it_needs_to_stop

                            else -> R.drawable.the_simplest_daily_routine_for_self_improvement
                        }
                    ),
                    contentDescription = video.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = video.title,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}
