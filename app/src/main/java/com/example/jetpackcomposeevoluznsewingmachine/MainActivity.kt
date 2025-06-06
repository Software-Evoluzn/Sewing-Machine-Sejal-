package com.example.jetpackcomposeevoluznsewingmachine

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeevoluznsewingmachine.Screens.BreakDownScreen
import com.example.jetpackcomposeevoluznsewingmachine.Screens.DashBoardLiveScreen
import com.example.jetpackcomposeevoluznsewingmachine.Screens.DifferentKeyboardTypes
import com.example.jetpackcomposeevoluznsewingmachine.Screens.EnterPasswordScreen
import com.example.jetpackcomposeevoluznsewingmachine.Screens.MachineRuntime
import com.example.jetpackcomposeevoluznsewingmachine.Screens.MainMenu
import com.example.jetpackcomposeevoluznsewingmachine.Screens.MaintenanceScreen
import com.example.jetpackcomposeevoluznsewingmachine.Screens.OilLevelGraph
import com.example.jetpackcomposeevoluznsewingmachine.Screens.PreventiveMaintenanceScreen
import com.example.jetpackcomposeevoluznsewingmachine.Screens.ProductionEfficiencyScreen
import com.example.jetpackcomposeevoluznsewingmachine.Screens.ShowingCombineGraphs
import com.example.jetpackcomposeevoluznsewingmachine.Screens.StarterPasswordScreen
import com.example.jetpackcomposeevoluznsewingmachine.Screens.TemperatureGraph
import com.example.jetpackcomposeevoluznsewingmachine.Screens.VibrationGraph
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.ui.theme.JetpackComposeEvoluznSewingMachineTheme
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    lateinit var notificationAndSoundClass:NotificationAndSoundHelpherClass
    lateinit var context:Context
    private var showUsbDetachedDialog by mutableStateOf(false)

    private val usbDetachedReceiver=object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
           if(intent?.action=="USB_DEVICE_DEATTACHED"){
               showUsbDetachedDialog=true
           }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context=applicationContext
        notificationAndSoundClass=NotificationAndSoundHelpherClass()

        notificationAndSoundClass.initBuzzerSound(this)

        // Start the USB Serial Service
        val serviceIntent = Intent(this, UsbSerialService::class.java)
        startService(serviceIntent)

        val intent = Intent(context, RuntimeMonitorService::class.java)
        context.startForegroundService(intent)
//

        enableEdgeToEdge()
        setContent {

            var snackBar= remember {SnackbarHostState()}
            JetpackComposeEvoluznSewingMachineTheme {
                Surface(
                    modifier=Modifier.fillMaxSize()
                        .padding(WindowInsets.systemBars.asPaddingValues()) ,
                    color=MaterialTheme.colorScheme.background){

                    Scaffold(
                        snackbarHost = {SnackbarHost(hostState = snackBar)}
                    ) { paddingValues->


                        AppNavigation(snackBar)
                        if (showUsbDetachedDialog) {
                            AlertDialog(onDismissRequest = { showUsbDetachedDialog = false },
                                title = { Text("USB Disconnected") },
                                text = { Text("The Usb Device Disconnected") },
                                confirmButton = {
                                    TextButton(onClick = { showUsbDetachedDialog = false }) {
                                        Text("OK")
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(usbDetachedReceiver, IntentFilter("USB_DEVICE_DEATTACHED"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(usbDetachedReceiver)
    }

    override fun onStop() {
        super.onStop()
        DatabaseBackupHelper.backupDatabase(this)  // ⬅️ Backup happens here
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationAndSoundClass.releaseSoundPool()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(snackBarHostState:SnackbarHostState){
    val navController= rememberNavController()



    val viewModel: MachineViewModel = viewModel()
    val todayTemps by viewModel.todayTemperatureList.observeAsState(emptyList())
    val todayVibration by viewModel.todayVibrationList.observeAsState(emptyList())
    val todayOilLevelList by viewModel.todayOilLevelList.observeAsState(emptyList())
    val todayRuntimeList by viewModel.todayRuntimeList.observeAsState(emptyList())
    val todayIdleTimeList by viewModel.todayIdleTimeList.observeAsState(emptyList())

    val weeklyTemps by viewModel.weeklyTemperatureList.observeAsState(emptyList())
    val weeklyVibrationList by viewModel.weeklyVibrationList.observeAsState(emptyList())
    val weeklyOilLevelList by viewModel.weeklyOilLevelList.observeAsState(emptyList())
    val weeklyRunTimeList by viewModel.weeklyRunTimeList.observeAsState(emptyList())
    val weeklyIdleTimeList by viewModel.weeklyIdleTimeList.observeAsState(emptyList())





    NavHost(navController = navController, startDestination = "dashBoardScreen"){
        composable("mainMenu"){ MainMenu(navController) }
        composable("dashBoardScreen"){ DashBoardLiveScreen(navController) }
        composable("machineRuntimeScreen"){ MachineRuntime(navController) }
        composable("maintenanceScreen"){ MaintenanceScreen(navController) }
        composable("preventiveMaintenance"){PreventiveMaintenanceScreen(navController)}
        composable("productionEfficiency"){ProductionEfficiencyScreen(navController)}
        composable("breakdownScreen"){BreakDownScreen(navController)}
        composable("showCombineGraphScreen"){ShowingCombineGraphs(
            navController=navController,
            GraphHeading = "SHOWING REAL TIME DATA",
            onBack = {navController.popBackStack()}
            )}
        composable("starter"){ StarterPasswordScreen(navController) }
        composable("enterPassword") { EnterPasswordScreen(navController) }
        composable("setPassword"){ DifferentKeyboardTypes(navController) }

        composable("temperatureGraph") {

            TemperatureGraph(
                navController = navController,
                modifier = Modifier,
                onBack = { navController.popBackStack() },
                GraphHeading = "Temperature Graph",
                dataLabel = "Temperature Graph in \u00B0C",
                todayTemps = todayTemps,
                weeklyTemps = weeklyTemps,
                valueColor = Color(0xFFF44336),
                unit="°C",
                snackBarHostState=snackBarHostState,
                threshHold = 45.0,
                shouldTriggerAlert = { it > 45.0 },
                alertMessage = "🌡️ Temperature is above 45°C"
            )
        }
        composable("vibrationGraphScreen"){

            VibrationGraph(
                navController= navController,
                modifier=Modifier,
                onBack={navController.popBackStack()},
                GraphHeading = "Vibration Graph",
                dataLabel = "Vibration Graph in mm/s",
                todayTemps = todayVibration,
                weeklyTemps = weeklyVibrationList,
                valueColor = Color(0xFFFF7B00),
                unit="mm/s",
                snackBarHostState=snackBarHostState,
                threshHold = 45.0,
                shouldTriggerAlert = { it > 45.0 },
                alertMessage = "⚠\uFE0F Vibration level exceeded 45 mm/s"

        ) }
        composable("oilLevelGraphScreen"){
            OilLevelGraph(
                navController= navController,
                modifier=Modifier,
                onBack={navController.popBackStack()},
                GraphHeading = "Oil Level  Graph",
                dataLabel = "OilLevel Graph in mm/s",
                todayTemps = todayOilLevelList,
                weeklyTemps = weeklyOilLevelList,
                valueColor = Color(0xFF0BA911),
                unit="%",
                snackBarHostState=snackBarHostState,
                threshHold = 30.0,
                shouldTriggerAlert = { it < 30.0 },
                alertMessage = "⚠\uFE0F oil  level less than  30 %"


            )
        }
//        composable("runTimeAnalysisGraphScreen"){
//            RunTimeAnalysisGraph(
//                navController= navController,
//                modifier=Modifier,
//                onBack={navController.popBackStack()},
//                GraphHeading = "RunTime Analysis  Graph",
//                dataLabel = "RunTime Analysis Graph in hrs",
//                todayTemps = todayRuntimeList,
//                weeklyTemps = weeklyRunTimeList,
//                valueColor = Color(0xFF3386FF),
//                unit="hrs",
//                snackBarHostState=snackBarHostState,
//                threshHold = 45.0,
//                shouldTriggerAlert = { it > 45.0 },
//                alertMessage = "⚠\uFE0F Vibration level exceeded 45 mm/s"
//        ) }
//        composable("idleTimeAnalysisGraphScreen"){
//            IdleTimeAnalysisGraph(
//                navController= navController,
//                modifier=Modifier,
//                onBack={navController.popBackStack()},
//                GraphHeading = "IdleTime Analysis Graph",
//                dataLabel = "IdleTime Analysis Graph in mm/s",
//                todayTemps = todayIdleTimeList,
//                weeklyTemps = weeklyIdleTimeList,
//                valueColor = Color(0xFF8569D8),
//                unit="hrs",
//                snackBarHostState=snackBarHostState,
//                threshHold = 45.0,
//                shouldTriggerAlert = { it > 45.0 },
//                alertMessage = "⚠\uFE0F Vibration level exceeded 45 mm/s"
//            ) }
    }
}


