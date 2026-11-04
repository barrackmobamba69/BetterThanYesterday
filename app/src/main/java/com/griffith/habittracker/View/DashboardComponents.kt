// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.View

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.griffith.habittracker.Controller.EmergencyController
import com.griffith.habittracker.Controller.StreakController
import android.content.Context
import androidx.compose.ui.platform.LocalContext



@Composable
fun StreakCounterCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                TimeDisplay(value = StreakController.days.value, label = "DAYS")

                // Hours
                TimeDisplay(value = StreakController.hours.value, label = "HRS")

                // Minutes
                TimeDisplay(value = StreakController.minutes.value, label = "MINS")

                // Seconds
                TimeDisplay(value = StreakController.seconds.value, label = "SECS")
            }
        }
    }
}

@Composable
private fun TimeDisplay(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Text(label)
    }
}

@Composable
fun RelapseButton() {
    val context = LocalContext.current

    Button(
        onClick = {
            StreakController.recordRelapse(context)
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
}

@Composable
fun EmergencyButton() {
    val context = LocalContext.current
    Button(
        onClick = { EmergencyController.showEmergencySupport(context) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text("Emergency")
    }
}

@Composable
fun EmergencyDialog() {
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
}