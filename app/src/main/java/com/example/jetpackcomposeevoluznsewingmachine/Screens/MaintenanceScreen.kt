package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.PreventiveAndProductionDataClass
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.ProductionCartItemList
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



    val productionCardListItem=listOf(
        ProductionCartItemList(
            title ="TEMPERATURE",
            value ="${latestTemp ?: 0}",
            unit ="°C" ,
            icon = painterResource(R.drawable.temp),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("temperatureGraph")},
            valueColor = Color(0xFFF44336)
        ),
        ProductionCartItemList(
            title ="VIBRATION",
            value ="${latestVib ?: 0}",
            unit ="mm/s" ,
            icon = painterResource(R.drawable.vib),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("vibrationGraphScreen")},
            valueColor = Color(0xFFFF5722)
        ),
        ProductionCartItemList(
            title ="OIL LEVEL",
            value ="${latestOilLevel ?: 0}",
            unit ="%" ,
            icon = painterResource(R.drawable.oil_level),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("oilLevelGraphScreen")},
            valueColor = Color(0xFF4CAF50)
        ),
        PreventiveAndProductionDataClass(
            title ="PREVENTIVE MAINTENANCE",

            icon = painterResource(R.drawable.settings),
            arrowIcon = painterResource(R.drawable.btn_image),
            arrowIconClick = {navController.navigate("preventiveMaintenance")},

        )

    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val dmRegular = FontFamily(Font(R.font.dmsans_regular))

        Box(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Image(
                painter = painterResource(R.drawable.aquarelle_logo),
                contentDescription = "logo",
                modifier = Modifier.size(70.dp).align(Alignment.TopStart)
            )
            Text(
                text = "MAINTENANCE ",
                fontSize = 24.sp,
                fontFamily = dmRegular,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        // Middle content (cards) centered
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {

                LazyVerticalGrid(columns= GridCells.Fixed(2),
                    modifier=Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    itemsIndexed(productionCardListItem) {index, card ->
                        if(index==3 && card is PreventiveAndProductionDataClass){
                            MaintenanceCard(
                                title = card.title,
                                icon = card.icon,
                                arrowIcon = card.arrowIcon,
                                onArrowClick = card.arrowIconClick
                            )
                        }else if(card is ProductionCartItemList) {
                            ParameterBox(
                                title = card.title,
                                value = card.value,
                                unit = card.unit,
                                icon = card.icon,
                                arrowIcon = card.arrowIcon,
                                onClick = card.onClick,
                                valueColor = card.valueColor

                            )
                        }
                    }
                }
            } else {

                LazyVerticalGrid(columns= GridCells.Fixed(2),
                    modifier=Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    itemsIndexed(productionCardListItem) { index, card ->
                        if (index == 3 && card is PreventiveAndProductionDataClass) {
                            MaintenanceCard(
                                title = card.title,
                                icon = card.icon,
                                arrowIcon = card.arrowIcon,
                                onArrowClick = card.arrowIconClick
                            )
                        } else if (card is ProductionCartItemList) {
                            ParameterBox(
                                title = card.title,
                                value = card.value,
                                unit = card.unit,
                                icon = card.icon,
                                arrowIcon = card.arrowIcon,
                                onClick = card.onClick,
                                valueColor = card.valueColor

                            )
                        }
                    }
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
            .defaultMinSize(minWidth = 80.dp)
            .height(155.dp)
            .border(0.5.dp, Color(0xFF283593), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp)
        ) {

            // Arrow at top-right
            Image(
                painter = arrowIcon,
                contentDescription = "Forward",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(37.dp)
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
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 30.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 55.sp,
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



