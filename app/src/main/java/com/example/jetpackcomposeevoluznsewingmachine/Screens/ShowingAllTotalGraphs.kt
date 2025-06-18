package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

//idle time
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IdleTimeAnalysisGraph(navController: NavController,
                          modifier: Modifier,
                          onBack: () -> Unit,
                          GraphHeading: String,
                          dataLabel:String,
                          todayTemps: List<Double>,
                          weeklyTemps: List<Double>,
                          valueColor: Color,
                          unit:String,
                          snackBarHostState:SnackbarHostState,
                          threshHold:Double,
                          shouldTriggerAlert:(Double)->Boolean,
                          alertMessage:String,
                          alertTitle:String
) {
    TemperatureGraph(navController,
        modifier,
        onBack ,
        GraphHeading,
        dataLabel,
        todayTemps,
        weeklyTemps,
        valueColor,
        unit,
        snackBarHostState=snackBarHostState,
        threshHold=threshHold,
        shouldTriggerAlert = shouldTriggerAlert,
        alertMessage=alertMessage,
        alertTitle = alertTitle
        )
}


//oil level
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OilLevelGraph(navController: NavController,
                  modifier: Modifier,
                  onBack: () -> Unit,
                  GraphHeading: String,
                  dataLabel:String,
                  todayTemps: List<Double>,
                  weeklyTemps: List<Double>,
                  valueColor: Color,
                  unit:String,
                  snackBarHostState:SnackbarHostState,
                  threshHold:Double,
                  shouldTriggerAlert:(Double)->Boolean,
                  alertMessage:String,
                  alertTitle:String
) {
    TemperatureGraph(navController,
        modifier,
        onBack ,
        GraphHeading,
        dataLabel,
        todayTemps,
        weeklyTemps,
        valueColor,
        unit,
        snackBarHostState = snackBarHostState,
        threshHold=threshHold,
        shouldTriggerAlert = shouldTriggerAlert,
        alertMessage=alertMessage,
        alertTitle=alertTitle
        )
}



//run time
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RunTimeAnalysisGraph(navController: NavController,
                         modifier: Modifier,
                         onBack: () -> Unit,
                         GraphHeading: String,
                         dataLabel:String,
                         todayTemps: List<Double>,
                         weeklyTemps: List<Double>,
                         valueColor: Color,
                         unit:String,
                         snackBarHostState:SnackbarHostState,
                         threshHold:Double,
                         shouldTriggerAlert:(Double)->Boolean,
                         alertMessage:String,
                         alertTitle: String
) {
    TemperatureGraph(navController,
        modifier,
        onBack ,
        GraphHeading,
        dataLabel,
        todayTemps,
        weeklyTemps,
        valueColor,
        unit,
        snackBarHostState=snackBarHostState,
        threshHold=threshHold,
        shouldTriggerAlert = shouldTriggerAlert,
        alertMessage=alertMessage,
        alertTitle = alertTitle)
}

//vibration
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
                   unit:String,
                   snackBarHostState:SnackbarHostState,
                   threshHold:Double,
                   shouldTriggerAlert:(Double)->Boolean,
                   alertMessage:String,
                   alertTitle: String
) {
    TemperatureGraph(navController,
        modifier,
        onBack ,
        GraphHeading,
        dataLabel,
        todayTemps,
        weeklyTemps,
        valueColor,
        unit,
        snackBarHostState = snackBarHostState,
        threshHold=threshHold,
        shouldTriggerAlert = shouldTriggerAlert,
        alertMessage=alertMessage,
        alertTitle = alertTitle)
}