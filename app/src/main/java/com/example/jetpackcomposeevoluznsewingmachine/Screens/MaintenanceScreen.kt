package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
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
fun MaintenanceScreen(navController: NavController) {

    val viewModel: MachineViewModel = viewModel()

//    val latestData by viewModel.latestData.observeAsState()
    val latestTemp by viewModel.latestTempValue.observeAsState()
    val latestVib by viewModel.latestVibValue.observeAsState()
    val latestOilLevel by viewModel.latestOilLevelValue.observeAsState()
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
            text = "MAINTENANCE PARAMETERS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = dmRegular,
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
                        title = "TEMPERATURE ",
                        value="${latestTemp ?: 0}",
                        unit="°C",
                        icon = painterResource(R.drawable.temp),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("temperatureGraph") },
                        valueColor = Color(0xFFEE5D50)
                    )

                    ParameterBox(
                        title = "VIBRATION ",
                        value="${latestVib ?: 0}",
                        unit="mm/s",
                        icon = painterResource(R.drawable.vib),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("vibrationGraphScreen") },
                        valueColor = Color(0xFF29B6F6)
                    )


                    ParameterBox(
                        title = "OIL LEVEL ",
                        value="${latestOilLevel ?: 0}",
                        unit="mm/s",
                        icon = painterResource(R.drawable.oil_level),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("oilLevelGraphScreen") },
                        valueColor = Color(0xFF29B6F6)
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp)
                ) {
                    ParameterBox(
                        title = "TEMPERATURE ",
                        value="${latestTemp ?: 0}",
                        unit="°C",
                        icon = painterResource(R.drawable.temp),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("temperatureGraph") },
                        valueColor = Color(0xFFEE5D50),
                        modifier = Modifier.weight(0.5f)
                    )
                    Spacer(modifier = Modifier.width(32.dp))  // Space between the two cards
                    ParameterBox(
                        title = "VIBRATION ",
                        value="${latestVib ?: 0}",
                        unit="mm/s",
                        icon = painterResource(R.drawable.vib),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("vibrationGraphScreen") },
                        valueColor = Color(0xFFFF7B00),
                        modifier = Modifier.weight(0.5f)
                    )
                    Spacer(modifier = Modifier.width(32.dp))  // Space between the two cards

                    ParameterBox(
                        title = "OIL LEVEL ",
                        value="${latestOilLevel ?: 0}",
                        unit="mm/s",
                        icon = painterResource(R.drawable.oil_level),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onClick = { navController.navigate("oilLevelGraphScreen") },
                        valueColor = Color(0xFF0BA911),
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

@Preview(showSystemUi = true)
@Composable
fun PreviewFunctionMaintenance(){
    val navController = rememberNavController()  // create dummy controller
    MaintenanceScreen(navController = navController)
}

//@Preview
//@Composable
//fun CardPreviewunction(){
//    val navController= rememberNavController()
//    ParameterBox("RUN TIME",
//        "23.6",
//        "\u00B0C",
//        painterResource(R.drawable.run_time),
//        painterResource(R.drawable.btn_image),
//        onClick = { navController.navigate("machineRuntimeScreen") },
//
//    )
//}

@Composable
fun ParameterBox(
    title: String,
    value: String,
    unit: String,
    icon: Painter,                // Icon left of the title
    arrowIcon: Painter,           // Arrow icon top-right
    onClick: () -> Unit,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    // Animation trigger state
    var startAnimation by remember { mutableStateOf(false) }

    // Smooth scale animation from 0.8f to 1f
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "parameterBoxScale"
    )

    // Trigger animation when this composable enters composition
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .padding(8.dp)
            .defaultMinSize(minWidth = 150.dp)
            .height(200.dp)
            .border(0.5.dp, Color(0xFF283593), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            // Arrow at top-right
            Image(
                painter = arrowIcon,
                contentDescription = "Forward",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(30.dp)
                    .clickable { onClick() }
            )

            // Icon and Title at top-left
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2B3674),
                    fontFamily = dmRegular
                )
            }

            // Value and Unit at center
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.align(Alignment.Center)
                    .padding(top=30.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = valueColor
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = unit,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4C4C4C),
                    modifier = Modifier.padding(bottom = 5.dp),
                    fontFamily = dmRegular
                )
            }
        }
    }
}



