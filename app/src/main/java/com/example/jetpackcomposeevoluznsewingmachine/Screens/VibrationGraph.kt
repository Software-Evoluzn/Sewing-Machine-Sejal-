package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VibrationGraph(navController: NavController,
                   modifier: Modifier,
                   onBack: () -> Unit,
                   GraphHeading: String,
                   dataLabel:String,
                   todayTemps: List<Double>,
                   weeklyTemps: List<Double>,
                   valueColor: Color,
                   unit:String
) {
   TemperatureGraph(navController,
       modifier,
       onBack ,
       GraphHeading,
       dataLabel,
       todayTemps,
       weeklyTemps,
       valueColor,
       unit)
}