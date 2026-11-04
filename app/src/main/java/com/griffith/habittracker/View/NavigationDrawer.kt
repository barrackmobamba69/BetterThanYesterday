// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    navController: NavHostController,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    content: @Composable () -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Menu", modifier = Modifier.padding(16.dp))
                }

                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = currentRoute == "dashboard",
                    onClick = { navController.navigate("dashboard") { launchSingleTop = true } }
                )
                NavigationDrawerItem(
                    label = { Text("Meditation") },
                    selected = currentRoute == "meditation",
                    onClick = { navController.navigate("meditation") { launchSingleTop = true } }
                )
                NavigationDrawerItem(
                    label = { Text("Stats") },
                    selected = currentRoute == "stats",
                    onClick = { navController.navigate("stats") { launchSingleTop = true } }
                )
                NavigationDrawerItem(
                    label = { Text("Chatbot") },
                    selected = currentRoute == "chatbot",
                    onClick = { navController.navigate("chatbot") { launchSingleTop = true } }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = currentRoute == "settings",
                    onClick = { navController.navigate("settings") { launchSingleTop = true } }
                )
                NavigationDrawerItem(
                    label = { Text("Login/Register") },
                    selected = currentRoute == "login/register",
                    onClick = { navController.navigate("login/register") { launchSingleTop = true } }
                )
            }
        }
    ) {
        content() // Displays the screen content inside the navigation drawer
    }
}