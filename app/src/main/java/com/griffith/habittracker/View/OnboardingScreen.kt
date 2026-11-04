// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.View

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.griffith.habittracker.Controller.StreakController
import com.griffith.habittracker.Model.UserPreferences

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun OnboardingNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Load streak data when app starts
    LaunchedEffect(Unit) {
        StreakController.loadStreak(context)
    }

    NavHost(navController = navController, startDestination = "onboardingScreenOne") {
        composable("onboardingScreenOne") {
            OnboardingScreenOne(navController, UserPreferences.isOnboardingCompleted(context))
        }
        composable("onboardingScreenTwo") {
            OnboardingScreenTwo(navController)
        }
        composable("dashboard") {
            DashboardScreen(navController)
        }
        composable("meditation") {
            MeditationScreen(navController)
        }
        composable("stats") {
            StatsScreen(navController)
        }
        composable("chatbot") {
            ChatbotScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("login/register") {
            LoginScreen(navController)
        }
    }
}

@Composable
fun OnboardingScreenOne(navController: NavController, detailsCompleted: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // Top section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Welcome to BetterThanYesterday",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Break bad habits, build better routines, and stay motivated on your journey to positive change.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Middle section - Features
        Column {
            Text(
                text = "Purpose of BetterThanYesterday",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🔥 Track your progress with real-time counters",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "💪 Get instant support when you need it most",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "🧘 Access meditation and mindfulness tools",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "📊 See your improvement with detailed statistics",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Bottom section - Button
        Column {
            Button(
                onClick = {
                    try {
                        if (detailsCompleted) {
                            navController.navigate("dashboard")
                        } else {
                            navController.navigate("onboardingScreenTwo")
                        }
                    } catch (e: Exception) {
                        Log.e("Navigation", "Error navigating: ${e.message}", e)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (detailsCompleted) "Continue to Dashboard" else "Get Started",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OnboardingScreenTwo(navController: NavController) {
    val habit = remember { mutableStateOf("") }
    val reason = remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // Top section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tell Us About Your Goals",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "This helps us personalize your experience and provide better support.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Middle section - Form
        Column {
            Text(
                text = "What habit would you like to change?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = habit.value,
                onValueChange = { habit.value = it },
                placeholder = {
                    Text(
                        "e.g., Smoking, Drinking...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Why is this change important to you?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = reason.value,
                onValueChange = { reason.value = it },
                placeholder = {
                    Text(
                        "e.g., For my health, To save money",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        }

        // Bottom section - Buttons
        Column {
            Button(
                onClick = {
                    UserPreferences.saveUserDetails(context, habit.value, reason.value)
                    StreakController.startStreak(context)
                    navController.navigate("dashboard")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = habit.value.isNotBlank() && reason.value.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Start My Journey",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { navController.navigate("onboardingScreenOne") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}