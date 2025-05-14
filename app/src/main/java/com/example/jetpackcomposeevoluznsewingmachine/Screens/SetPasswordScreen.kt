package com.example.jetpackcomposeevoluznsewingmachine.Screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeevoluznsewingmachine.PasswordDataStore
import com.example.jetpackcomposeevoluznsewingmachine.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.*

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SetPasswordScreen() {
    val navController= rememberNavController()
    DifferentKeyboardTypes(navController)

}

@Composable
fun DifferentKeyboardTypes(navController: NavController) {
    var newPassword by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val context = LocalContext.current
    val passwordsMatch = newPassword == password && newPassword.isNotBlank()

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Your New Password",
            fontFamily = dmRegular,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Enter New Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(380.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Repeat New Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(380.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        if (!passwordsMatch && password.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Passwords do not match",
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (newPassword.isNotBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        PasswordDataStore.savePassword(context, newPassword)
                        withContext(Dispatchers.Main) {
                            navController.navigate("enterPassword") {
                                popUpTo("starter") { inclusive = true }
                                popUpTo("setPassword") { inclusive = true }
                            }
                        }
                    }
                }
            },
            enabled = passwordsMatch,
            modifier = Modifier
                .width(200.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp), // No elevation
            border = BorderStroke(1.dp, Color(0xFF5E69B4)) // Border color and thickness
        ) {
            Text(text = "Submit", fontSize = 16.sp)
        }
    }
}

@Composable
fun EnterPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val passwordFlow = remember { PasswordDataStore.getPasswordFlow(context) }
    val savedPassword by passwordFlow.collectAsState(initial = null)
    var enteredPassword by remember { mutableStateOf("") }
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Your Password",
            fontSize = 18.sp,
            fontFamily = dmRegular,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = enteredPassword,
            onValueChange = { enteredPassword = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .width(280.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (enteredPassword == savedPassword) {
                    navController.navigate("machineRuntimeScreen") {
                        popUpTo("enterPassword") { inclusive = true }
                        popUpTo("setPassword") { inclusive = true }
                        popUpTo("starter") { inclusive = true }
                    }
                } else {
                    showDialog=true

                }
            },
            modifier = Modifier
                .width(200.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp), // No elevation
            border = BorderStroke(1.dp, Color(0xFF5E69B4)) // Border color and thickness
        ) {
            Text(text = "Continue", fontSize = 16.sp)
        }
    }

    // Show dialog on incorrect password
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Invalid Password!!") },
            text = { Text("Incorrect Password") },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)
                ) {
                    Text("OK")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}



