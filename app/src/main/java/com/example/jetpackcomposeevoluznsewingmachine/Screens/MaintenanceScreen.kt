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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.PreventiveAndProductionDataClass
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.ProductionCartItemList
import com.example.jetpackcomposeevoluznsewingmachine.R


@Composable
fun MaintenanceScreen(navController: NavController) {
    val viewModel: MachineViewModel = viewModel()

    // Fixed configuration usage
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    val latestTemp by viewModel.latestTempValue.observeAsState()
    val sanitizedTemp = latestTemp?.takeIf { it >= 0 } ?: 0
    val latestVib by viewModel.latestVibValue.observeAsState()
    val latestOilLevel by viewModel.latestOilLevelValue.observeAsState()

    // Fixed list creation - using consistent types
    val productionCardListItem = listOf(
        ProductionCartItemList(
            title = "TEMPERATURE",
            value = String.format("%.1f", sanitizedTemp.toFloat()),
            unit = "°C",
            icon = painterResource(R.drawable.temp),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = { navController.navigate("temperatureGraph") },
            valueColor = Color(0xFFF44336)
        ),
        ProductionCartItemList(
            title = "VIBRATION",
            value = "${latestVib ?: 0}",
            unit = "mm/s",
            icon = painterResource(R.drawable.vib),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = { navController.navigate("vibrationGraphScreen") },
            valueColor = Color(0xFFFF5722)
        ),
        ProductionCartItemList(
            title = "OIL LEVEL",
            value = "${latestOilLevel ?: 0}",
            unit = "%",
            icon = painterResource(R.drawable.oil_level),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = { navController.navigate("oilLevelGraphScreen") },
            valueColor = Color(0xFF4CAF50)
        )
    )

    // Separate preventive maintenance item
    val preventiveMaintenanceItem = PreventiveAndProductionDataClass(
        title = "PREVENTIVE MAINTENANCE",
        icon = painterResource(R.drawable.settings),
        arrowIcon = painterResource(R.drawable.btn_image),
        arrowIconClick = { navController.navigate("preventiveMaintenance") }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isPortrait) 12.dp else 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val dmRegular = FontFamily(Font(R.font.dmsans_regular))

        // Header Section
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.aquarelle_logo),
                contentDescription = "logo",
                modifier = Modifier
                    .size(if (isPortrait) 60.dp else 70.dp)
                    .align(Alignment.TopStart)
            )
            Text(
                text = "MAINTENANCE",
                fontSize = if (isPortrait) 20.sp else 24.sp,
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isPortrait) 4.dp else 8.dp),
                contentPadding = PaddingValues(if (isPortrait) 4.dp else 8.dp),
                verticalArrangement = Arrangement.spacedBy(if (isPortrait) 6.dp else 8.dp),
                horizontalArrangement = Arrangement.spacedBy(if (isPortrait) 6.dp else 8.dp)
            ) {
                // Add production cards
                itemsIndexed(productionCardListItem) { _, card ->
                    ParameterBox(
                        title = card.title,
                        value = card.value,
                        unit = card.unit,
                        icon = card.icon,
                        arrowIcon = card.arrowIcon,
                        onClick = card.onClick,
                        valueColor = card.valueColor,
                        isPortrait = isPortrait
                    )
                }

                // Add preventive maintenance card as last item
                item {
                    MaintenanceCard(
                        title = preventiveMaintenanceItem.title,
                        icon = preventiveMaintenanceItem.icon,
                        arrowIcon = preventiveMaintenanceItem.arrowIcon,
                        onArrowClick = preventiveMaintenanceItem.arrowIconClick,
                        isPortrait = isPortrait
                    )
                }
            }
        }

        // Footer Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Powered by ",
                fontSize = if (isPortrait) 12.sp else 15.sp,
                fontWeight = FontWeight.Thin,
                fontFamily = dmRegular,
                color = Color(0xFF424242)
            )
            Text(
                text = "EVOLUZN",
                fontSize = if (isPortrait) 15.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmRegular,
                color = Color(0xFF424242)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewFunctionMaintenance() {
    val navController = rememberNavController()
    MaintenanceScreen(navController = navController)
}

@Composable
fun ParameterBox(
    title: String,
    value: String,
    unit: String,
    icon: Painter,
    arrowIcon: Painter?,
    onClick: () -> Unit,
    valueColor: Color,
    isPortrait: Boolean = true,
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
            .padding(if (isPortrait) 4.dp else 8.dp)
            .defaultMinSize(minWidth = if (isPortrait) 60.dp else 80.dp)
            .height(if (isPortrait) 140.dp else 155.dp)
            .border(0.5.dp, Color(0xFF283593), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPortrait) 16.dp else 25.dp)
        ) {
            // Arrow at top-right
            if (arrowIcon != null) {
                Image(
                    painter = arrowIcon,
                    contentDescription = "Forward",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(if (isPortrait) 28.dp else 37.dp)
                        .clickable { onClick() }
                )
            }

            // Icon and Title at top-left
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(if (isPortrait) 28.dp else 35.dp)
                )

                Spacer(modifier = Modifier.width(if (isPortrait) 4.dp else 6.dp))

                Text(
                    text = title,
                    fontSize = if (isPortrait) 11.sp else 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2B3674),
                    fontFamily = dmRegular,
                    maxLines = 2
                )
            }

            // Value and Unit at center
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = if (isPortrait) 20.dp else 30.dp)
            ) {
                Text(
                    text = value,
                    fontSize = if (isPortrait) 30.sp else 55.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = valueColor
                )

                Spacer(modifier = Modifier.width(if (isPortrait) 4.dp else 6.dp))

                Text(
                    text = unit,
                    fontSize = if (isPortrait) 16.sp else 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4C4C4C),
                    modifier = Modifier.padding(bottom = if (isPortrait) 3.dp else 5.dp),
                    fontFamily = dmRegular
                )
            }
        }
    }
}

@Composable
fun MaintenanceCard(
    title: String,
    icon: Painter,
    arrowIcon: Painter,
    onArrowClick: () -> Unit,
    isPortrait: Boolean = true,
    modifier: Modifier = Modifier
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    // Animation trigger state
    var startAnimation by remember { mutableStateOf(false) }

    // Smooth scale animation from 0.8f to 1f
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "maintenanceCardScale"
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
            .padding(if (isPortrait) 4.dp else 8.dp)
            .defaultMinSize(minWidth = if (isPortrait) 60.dp else 80.dp)
            .height(if (isPortrait) 140.dp else 155.dp)
            .border(0.5.dp, Color(0xFF283593), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPortrait) 16.dp else 25.dp)
        ) {
            // Arrow at top-right
            Image(
                painter = arrowIcon,
                contentDescription = "Forward",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(if (isPortrait) 28.dp else 37.dp)
                    .clickable { onArrowClick() }
            )

            // Icon and Title centered
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(if (isPortrait) 35.dp else 45.dp)
                )

                Spacer(modifier = Modifier.height(if (isPortrait) 8.dp else 12.dp))

                Text(
                    text = title,
                    fontSize = if (isPortrait) 12.sp else 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2B3674),
                    fontFamily = dmRegular,
                    maxLines = 2,
                    lineHeight = if (isPortrait) 14.sp else 16.sp
                )
            }
        }
    }
}

