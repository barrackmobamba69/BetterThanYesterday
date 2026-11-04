// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.View

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.griffith.habittracker.Controller.MeditationController
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import com.airbnb.lottie.LottieProperty
// Lottie imports
import com.airbnb.lottie.compose.*
import com.griffith.habittracker.R

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val controller = remember { MeditationController(context) }

    var selectedSound by remember { mutableStateOf("No Sound") } // Which sound user picked
    var selectedMinutes by remember { mutableStateOf(5) }  // How long to meditate
    var isTimerRunning by remember { mutableStateOf(false) }  // Is the timer running
    var isPaused by remember { mutableStateOf(false) } // Is the timer paused

    // Timer states
    var totalSeconds by remember { mutableStateOf(0) }
    var remainingSeconds by remember { mutableStateOf(0) }  // Remaining time
    var timerJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Calculate progress
    val progress = if (totalSeconds > 0) {
        (totalSeconds - remainingSeconds).toFloat() / totalSeconds.toFloat()
    } else 0f

    // Timer effect
    LaunchedEffect(isTimerRunning, isPaused) {
        if (isTimerRunning && !isPaused) {
            timerJob = launch {
                while (remainingSeconds > 0) {
                    delay(1000)  // Wait for 1 second
                    remainingSeconds--    // Subtract 1 second
                }
                // Timer finished
                if (remainingSeconds <= 0) { // If timer is finished, stop meditation
                    controller.stopSound()
                    isTimerRunning = false
                    isPaused = false
                }
            }
        } else {
            timerJob?.cancel()
        }
    }

    // Format time display
    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }

    // Ensures that the sound stops when user exits the screen
    DisposableEffect(Unit) {
        onDispose {
            controller.stopSound()
            timerJob?.cancel()
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
                            text = "Meditation",
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Timer Display Section
                if (isTimerRunning || remainingSeconds > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Meditation in Progress",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Large timer display
                            Text(
                                text = formatTime(remainingSeconds),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 48.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Sound: $selectedSound",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Progress Bar
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${((progress * 100).toInt())}% Complete",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Settings Section (only show when timer is not running)
                if (!isTimerRunning) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Sound selection column
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Select Sound",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            SoundNumberPicker(
                                controller = controller,
                                selectedSound = selectedSound,
                                onSoundSelected = {
                                    selectedSound = it
                                    controller.playSound(it)
                                }
                            )
                        }

                        // Time selection column
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Duration",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            TimeNumberPicker(
                                selectedMinutes = selectedMinutes,
                                onMinutesSelected = { selectedMinutes = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isTimerRunning) Arrangement.SpaceEvenly else Arrangement.Center
                ) {
                    if (!isTimerRunning) {
                        // Start Button
                        Button(
                            onClick = {
                                totalSeconds = selectedMinutes * 60
                                remainingSeconds = totalSeconds
                                isTimerRunning = true
                                isPaused = false
                                controller.playSound(selectedSound)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Meditation",
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                    else {
                        // Pause/Resume Button
                        Button(
                            onClick = {
                                isPaused = !isPaused
                                if (isPaused) {
                                    controller.stopSound()
                                }
                                else {
                                    controller.playSound(selectedSound)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPaused) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(if (isPaused) "Resume" else "Pause")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Stop Button
                        OutlinedButton(
                            onClick = {
                                controller.stopSound()
                                isTimerRunning = false
                                isPaused = false
                                remainingSeconds = 0
                                totalSeconds = 0
                                timerJob?.cancel()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {
                            Text("Stop")
                        }
                    }
                }

                // Add space between buttons and animation
                if (isTimerRunning) {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Lottie Breathing Exercise Section - Bigger and Simplified
                if (isTimerRunning) {
                    LottieBreathingAnimation(
                        isPlaying = isTimerRunning && !isPaused
                    )
                }

                // Session completion message
                if (remainingSeconds == 0 && totalSeconds > 0 && !isTimerRunning) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "🧘‍♀️ Well done, you have done really well!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Lottie Breathing Animation Component
@Composable
fun LottieBreathingAnimation(isPlaying: Boolean = true) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.breathing_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever,
        speed = 1.5f
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(400.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SoundNumberPicker(
    controller: MeditationController,
    selectedSound: String,
    onSoundSelected: (String) -> Unit
) {
    val soundOptions = controller.getSoundOptions()

    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                minValue = 0
                maxValue = soundOptions.size - 1
                displayedValues = soundOptions.toTypedArray()
                value = soundOptions.indexOf(selectedSound).coerceAtLeast(0)
                setTextColor(android.graphics.Color.WHITE)
                setOnValueChangedListener { _, _, newVal ->
                    onSoundSelected(soundOptions[newVal])
                }
            }
        },
        update = { picker ->
            picker.value = soundOptions.indexOf(selectedSound).coerceAtLeast(0)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TimeNumberPicker(
    selectedMinutes: Int,
    onMinutesSelected: (Int) -> Unit
) {
    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                minValue = 1
                maxValue = 60
                value = selectedMinutes
                setTextColor(android.graphics.Color.WHITE)
                setOnValueChangedListener { _, _, newVal ->
                    onMinutesSelected(newVal)
                }
            }
        },
        update = { picker ->
            picker.value = selectedMinutes
        }
    )
}



