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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.griffith.habittracker.Controller.AuthController

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    // UI state variables
    var isLogin by remember { mutableStateOf(true) } // Toggle between login/register mode
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") } // Only used for registration
    var passwordVisible by remember { mutableStateOf(false) } // For future password visibility toggle
    var errorMessage by remember { mutableStateOf("") } // Display validation errors
    var isLoading by remember { mutableStateOf(false) } // Show loading state during auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Welcome title - changes based on login/register mode
        Text(
            text = if (isLogin) "Welcome Back!" else "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Name field - only shown during registration
        if (!isLogin) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Disable input during authentication
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field with hidden text
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display error messages to user
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main action button - handles both login and registration
        Button(
            onClick = {
                errorMessage = ""
                isLoading = true

                if (isLogin) {
                    // Handle user login with existing credentials
                    if (AuthController.login(context, email, password)) {
                        navController.navigate("dashboard") {
                            popUpTo("login/register") { inclusive = true }
                        }
                    } else {
                        errorMessage = "Invalid email or password"
                    }
                } else {
                    // Handle new user registration with enhanced validation
                    val result = AuthController.registerWithValidation(context, email, password, name)
                    errorMessage = when (result) {
                        AuthController.RegisterResult.SUCCESS -> {
                            navController.navigate("dashboard") {
                                popUpTo("login/register") { inclusive = true }
                            }
                            ""
                        }
                        AuthController.RegisterResult.INVALID_EMAIL -> "Please enter a valid email address"
                        AuthController.RegisterResult.WEAK_PASSWORD -> "Password must be at least 6 characters"
                        AuthController.RegisterResult.INVALID_NAME -> "Please enter your full name"
                        AuthController.RegisterResult.USER_EXISTS -> "An account with this email already exists"
                        else -> "Registration failed. Please try again."
                    }
                }

                isLoading = false
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank() // Only enable when form is valid
        ) {
            if (isLoading) {
                // Show loading spinner and text during authentication
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...")
                }
            } else {
                Text(if (isLogin) "Login" else "Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle button to switch between login and registration modes
        TextButton(
            onClick = {
                isLogin = !isLogin
                errorMessage = "" // Clear any existing error messages
            },
            enabled = !isLoading // Prevent mode switching during authentication
        ) {
            Text(
                if (isLogin) "Don't have an account? Register"
                else "Already have an account? Login"
            )
        }
    }
}