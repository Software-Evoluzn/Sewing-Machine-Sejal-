package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.PasswordDataStore


@Composable
fun StarterPasswordScreen(navController: NavController) {
    val context  = LocalContext.current
    val passwordFlow = remember{PasswordDataStore.getPasswordFlow(context)}
    val password by passwordFlow.collectAsState(initial = null)

    LaunchedEffect(password) {
        if (!password.isNullOrBlank()) {
            navController.navigate("enterPassword") {
                popUpTo("enterPassword") { inclusive = true }
                popUpTo("starter") { inclusive = true }
                popUpTo("setPassword") { inclusive = true }
            }
        }
        else
        {
            navController.navigate("setPassword") {
                popUpTo("setPassword") { inclusive = true }
                popUpTo("starter") { inclusive = true }
            }
        }
    }


    Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
}