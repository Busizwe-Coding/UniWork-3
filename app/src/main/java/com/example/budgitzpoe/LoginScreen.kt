package com.example.budgitzpoe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    // Updated validation functions
    fun isNameValid(text: String) = text.matches(Regex("^[A-Za-z]+$")) // Only letters, no special chars or numbers
    fun isSurnameValid(text: String) = text.matches(Regex("^[A-Za-z]+$")) // Only letters, no special chars or numbers
    fun isUsernameValid(text: String) = text.matches(Regex("^[A-Za-z0-9]+$")) // No special characters
    fun isPasswordValid(text: String) = text.matches(Regex("^[A-Za-z0-9]+$")) // No special characters

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB6FF00)) // acid green
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Login / Register", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    // Only allow letters
                    if (it.matches(Regex("^[A-Za-z]*$"))) {
                        name = it
                    }
                },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = name.isNotEmpty() && !isNameValid(name)
            )

            OutlinedTextField(
                value = surname,
                onValueChange = {
                    // Only allow letters
                    if (it.matches(Regex("^[A-Za-z]*$"))) {
                        surname = it
                    }
                },
                label = { Text("Surname") },
                modifier = Modifier.fillMaxWidth(),
                isError = surname.isNotEmpty() && !isSurnameValid(surname)
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    // Only allow letters and numbers
                    if (it.matches(Regex("^[A-Za-z0-9]*$"))) {
                        username = it
                    }
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = username.isNotEmpty() && !isUsernameValid(username)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    // Only allow letters and numbers
                    if (it.matches(Regex("^[A-Za-z0-9]*$"))) {
                        password = it
                    }
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                isError = password.isNotEmpty() && !isPasswordValid(password)
            )

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {
                    error = ""

                    // Check for empty fields
                    if (name.isBlank() || surname.isBlank() || username.isBlank() || password.isBlank()) {
                        error = "No empty fields allowed"
                        return@Button
                    }

                    // Validate name (letters only, no special chars or numbers)
                    if (!isNameValid(name)) {
                        error = "Name must contain only letters (no special characters or numbers)"
                        return@Button
                    }

                    // Validate surname (letters only, no special chars or numbers)
                    if (!isSurnameValid(surname)) {
                        error = "Surname must contain only letters (no special characters or numbers)"
                        return@Button
                    }

                    // Validate username (no special characters)
                    if (!isUsernameValid(username)) {
                        error = "Username must contain only letters and numbers (no special characters)"
                        return@Button
                    }

                    // Validate password (no special characters)
                    if (!isPasswordValid(password)) {
                        error = "Password must contain only letters and numbers (no special characters)"
                        return@Button
                    }

                    onLoginSuccess()
                }) {
                    Text("Register")
                }

                Button(onClick = {
                    error = ""

                    // Check for empty fields
                    if (username.isBlank() || password.isBlank()) {
                        error = "No empty fields allowed"
                        return@Button
                    }

                    // Validate username format
                    if (!isUsernameValid(username)) {
                        error = "Username must contain only letters and numbers (no special characters)"
                        return@Button
                    }

                    // Validate password format
                    if (!isPasswordValid(password)) {
                        error = "Password must contain only letters and numbers (no special characters)"
                        return@Button
                    }

                    onLoginSuccess()
                }) {
                    Text("Login")
                }
            }
        }
    }
}