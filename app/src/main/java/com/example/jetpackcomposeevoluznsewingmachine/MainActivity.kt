package com.example.jetpackcomposeevoluznsewingmachine

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.jetpackcomposeevoluznsewingmachine.Screens.GraphScreenShowing
import com.example.jetpackcomposeevoluznsewingmachine.Screens.IdleTimeAnalysisGraph
import com.example.jetpackcomposeevoluznsewingmachine.Screens.MachineRuntime
import com.example.jetpackcomposeevoluznsewingmachine.Screens.MainMenu
import com.example.jetpackcomposeevoluznsewingmachine.Screens.MaintenanceScreen
import com.example.jetpackcomposeevoluznsewingmachine.Screens.OilLevelGraph
import com.example.jetpackcomposeevoluznsewingmachine.Screens.RunTimeAnalysisGraph
import com.example.jetpackcomposeevoluznsewingmachine.Screens.TemperatureGraph
import com.example.jetpackcomposeevoluznsewingmachine.Screens.VibrationGraph
import com.example.jetpackcomposeevoluznsewingmachine.ui.theme.JetpackComposeEvoluznSewingMachineTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the USB Serial Service
        val serviceIntent = Intent(this, UsbSerialService::class.java)
        startService(serviceIntent)

        enableEdgeToEdge()
        setContent {
            JetpackComposeEvoluznSewingMachineTheme {
                Surface(
                    modifier=Modifier.fillMaxSize()
                        .padding(WindowInsets.systemBars.asPaddingValues()) ,
                    color=MaterialTheme.colorScheme.background){
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(){
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination = "mainMenu"){
        composable("mainMenu"){ MainMenu(navController) }
        composable("machineRuntimeScreen"){ MachineRuntime(navController) }
        composable("maintenanceScreen"){ MaintenanceScreen(navController) }
        composable("temperatureGraph") {

            TemperatureGraph(
                navController = navController,
                modifier = Modifier,
                onBack = { navController.popBackStack() },
                "Temperature Graph"
            )
        }

        composable("GraphScreenShowing"){ GraphScreenShowing(navController,
            modifier = Modifier, onBackClick = {navController.popBackStack()},"Temperature Graph") }
        composable("vibrationGraphScreen"){ VibrationGraph(navController) }
        composable("oilLevelGraphScreen"){ OilLevelGraph(navController) }
        composable("runTimeAnalysisGraphScreen"){ RunTimeAnalysisGraph(navController) }
        composable("idleTimeAnalysisGraphScreen"){ IdleTimeAnalysisGraph(navController) }


    }

}


