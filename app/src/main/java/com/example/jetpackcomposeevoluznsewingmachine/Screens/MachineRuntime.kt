package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeevoluznsewingmachine.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.WindowInfo
import com.example.jetpackcomposeevoluznsewingmachine.rememberWindowInfo


@Composable
fun MachineRuntime(navController: NavController) {

    val viewModel: MachineViewModel = viewModel()

    val latestRunTimeData by viewModel.latestRunTime.observeAsState()
    val latestIdleTime by viewModel.todayIdleTime.observeAsState()

    val windowInfo = rememberWindowInfo()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val dmRegular = FontFamily(Font(R.font.dmsans_regular))

        Text(
            text = "MACHINE RUNTIME PARAMETERS",
            fontSize = 24.sp,
            fontFamily = dmRegular,
            fontWeight = FontWeight.Bold,
            color=Color(0xFF4B4B4B)
        )

        // Middle content (cards) centered
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ParameterBox(
                        title = "RUN TIME ",
                        value="${latestRunTimeData ?: 0}",
                        unit="hrs",
                        icon = painterResource(R.drawable.run_time),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("runTimeAnalysisGraphScreen") },
                        valueColor = Color(0xFF3386FF)

                    )

                    ParameterBox(
                        title = "IDLE TIME ",
                        value="${latestIdleTime ?: 0}",
                        unit="hrs",
                        icon = painterResource(R.drawable.idle_time),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("idleTimeAnalysisGraphScreen") },
                        valueColor = Color(0xFF8569D8)
                    )



                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp)
                ) {
                    ParameterBox(
                        title = "RUN TIME ",
                        value="${latestRunTimeData ?: 0}",
                        unit="hrs",
                        icon = painterResource(R.drawable.run_time),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("runTimeAnalysisGraphScreen") },
                        valueColor = Color(0xFF29B6F6),
                        modifier = Modifier.weight(0.5f)
                    )
                    Spacer(modifier = Modifier.width(32.dp))  // Space between the two cards
                    ParameterBox(
                        title = "IDLE TIME ",
                        value="${latestIdleTime ?: 0}",
                        unit="hrs",
                        icon = painterResource(R.drawable.idle_time),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("idleTimeAnalysisGraphScreen") },
                        valueColor = Color(0xFF8569D8),
                        modifier = Modifier.weight(0.5f)
                    )

                }


            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Powered by ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Thin,
                fontFamily = dmRegular,
                color= Color(0xFF424242)
            )
            Text(
                text = "EVOLUZN",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmRegular,
                color= Color(0xFF424242)
            )
        }
    }
}

@Preview
@Composable
fun PreviewFunctionMachineRuntime(){
    val navController= rememberNavController()
    MachineRuntime(navController)
}