// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.View

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.griffith.habittracker.Controller.AuthController
import com.griffith.habittracker.Controller.SettingsController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State variables
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showHabitDialog by remember { mutableStateOf(false) }
    var showReasonDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }

    // Settings state
    var username by remember { mutableStateOf(SettingsController.getUsername(context)) }
    var habit by remember { mutableStateOf(getUserHabit(context)) }
    var reason by remember { mutableStateOf(getUserReason(context)) }
    var isDarkMode by remember { mutableStateOf(SettingsController.isDarkMode(context)) }
    var notificationsEnabled by remember { mutableStateOf(SettingsController.areNotificationsEnabled(context)) }

    NavigationDrawer(
        navController = navController,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings",
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
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Section
                SectionTitle("Profile")

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Change Username
                        SettingsItemSimple(
                            title = "Change Username",
                            subtitle = "Current: $username",
                            onClick = { showUsernameDialog = true }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Change Habit
                        SettingsItemSimple(
                            title = "Change Habit",
                            subtitle = "Current: $habit",
                            onClick = { showHabitDialog = true }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Change Reason
                        SettingsItemSimple(
                            title = "Change Reason",
                            subtitle = "Current: $reason",
                            onClick = { showReasonDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Notifications Section
                SectionTitle("Notifications")

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Notifications Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Enable Notifications",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = {
                                    notificationsEnabled = it
                                    SettingsController.setNotificationsEnabled(context, it)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Support Section
                SectionTitle("Support")

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Help Center
                        SettingsItemSimple(
                            title = "Help Center",
                            onClick = {
                                // Implicit Intent - Open URL in browser
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
                                context.startActivity(intent)
                            }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Contact Support
                        SettingsItemSimple(
                            title = "Contact Support",
                            onClick = { showContactDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Data Management
                SectionTitle("WIP")

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Reset Journey Button
                        Button(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Restart Your Journey")
                        }

                        Text(
                            text = "This will clear all your progress data",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Add logout feature
                        Button(
                            onClick = {
                                AuthController.logout(context)
                                navController.navigate("login/register") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }

    // Username Dialog
    if (showUsernameDialog) {
        var newUsername by remember { mutableStateOf(username) }

        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            title = { Text("Change Username") },
            text = {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUsername.isNotBlank()) {
                            SettingsController.setUsername(context, newUsername)
                            username = newUsername
                            showUsernameDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUsernameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Habit Dialog
    if (showHabitDialog) {
        var newHabit by remember { mutableStateOf(habit) }

        AlertDialog(
            onDismissRequest = { showHabitDialog = false },
            title = { Text("Change Habit") },
            text = {
                OutlinedTextField(
                    value = newHabit,
                    onValueChange = { newHabit = it },
                    label = { Text("What habit are you quitting?") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHabit.isNotBlank()) {
                            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putString("habit", newHabit)
                                .apply()
                            habit = newHabit
                            showHabitDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showHabitDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Reason Dialog
    if (showReasonDialog) {
        var newReason by remember { mutableStateOf(reason) }

        AlertDialog(
            onDismissRequest = { showReasonDialog = false },
            title = { Text("Change Reason") },
            text = {
                OutlinedTextField(
                    value = newReason,
                    onValueChange = { newReason = it },
                    label = { Text("Why are you quitting?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newReason.isNotBlank()) {
                            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putString("reason", newReason)
                                .apply()
                            reason = newReason
                            showReasonDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showReasonDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Restart Your Journey") },
            text = {
                Text("This will reset all your progress data including streaks and tasks. This action cannot be undone. Are you sure?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        SettingsController.resetAppData(context)
                        showResetDialog = false
                        // Navigate back to onboarding
                        navController.navigate("onboardingScreenOne") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Contact Support Dialog
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Contact Support") },
            text = {
                Column {
                    Text("For any issues or feedback, please contact:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Email: support@habittracker.com")
                    Text("Twitter: @habittracker")
                }
            },
            confirmButton = {
                Button(onClick = { showContactDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsItemSimple(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper functions
private fun getUserHabit(context: Context): String {
    return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        .getString("habit", "your habit") ?: "your habit"
}

private fun getUserReason(context: Context): String {
    return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        .getString("reason", "your health") ?: "your health"
}