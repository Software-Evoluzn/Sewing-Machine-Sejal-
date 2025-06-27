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
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.ProductionCartItemList
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.MaintenanceLogViewModel
import com.example.jetpackcomposeevoluznsewingmachine.WindowInfo
import com.example.jetpackcomposeevoluznsewingmachine.rememberWindowInfo

@Composable
fun PreventiveMaintenanceScreen(navController: NavController) {


    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    val viewModel: MaintenanceLogViewModel = viewModel()
    val runtime by viewModel.runtimeState.observeAsState(0f)

    val displayRuntime = runtime.coerceIn(0f, 50f)
    val dueTime = (50 - displayRuntime).coerceAtLeast(0f)
    val preNotificationTime = if (displayRuntime >= 48f) (50 - displayRuntime) else 0f


    val productionCardListItem = listOf(
        ProductionCartItemList(
            title = "RUN TIME",
            value = displayRuntime.toInt().toString(),
            unit = "hrs",
            icon = painterResource(R.drawable.clock),
            arrowIcon = null,
            onClick = {},
            valueColor = Color(0xFFFFC107)
        ),
        ProductionCartItemList(
            title = "DUE TIME",
            value = dueTime.toInt().toString(),
            unit = "hrs",
            icon = painterResource(R.drawable.due_date),
            arrowIcon = null,
            onClick = {},
            valueColor = Color(0xFFFC5353)
        ),
        ProductionCartItemList(
            title = "PRE-NOTIFICATION TIME",
            value = preNotificationTime.toInt().toString(),
            unit = "hrs",
            icon = painterResource(R.drawable.active),
            arrowIcon = null,
            onClick = {},
            valueColor = Color(0xFF2196F3)
        )
    )

    val windowInfo = rememberWindowInfo()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

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
                text = "PREVENTIVE MAINTENANCE",
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


                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    items(productionCardListItem) { card ->
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
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    items(productionCardListItem) { card ->
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
                color = Color(0xFF424242)
            )
            Text(
                text = "EVOLUZN",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmRegular,
                color = Color(0xFF424242)
            )
        }
    }


}






@Composable
fun PreventiveAndProductionFun(
    title: String,
    value: String,
    unit: String,
    icon: Painter,
    arrowIcon: Painter,
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
            Image(
                painter = arrowIcon,
                contentDescription = "Forward",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(if (isPortrait) 28.dp else 37.dp)
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

