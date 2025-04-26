package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.annotation.InspectableProperty
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.WindowInfo
import com.example.jetpackcomposeevoluznsewingmachine.rememberWindowInfo


@Composable
fun MainMenu(navController: NavController) {

    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    val windowInfo = rememberWindowInfo()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "MAIN MENU",
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
                    MaintenanceCard(
                        title = "MAINTENANCE SECTION",
                        icon = painterResource(R.drawable.maintenance),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onArrowClick = { navController.navigate("maintenanceScreen") },

                    )

                    MaintenanceCard(
                        title = "MACHINE RUNTIME SECTION",
                        icon = painterResource(R.drawable.machine_logo),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onArrowClick = { navController.navigate("machineRuntimeScreen") }
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp)
                ) {
                    MaintenanceCard(
                        title = "MAINTENANCE SECTION",
                        icon = painterResource(R.drawable.maintenance),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onArrowClick = { navController.navigate("maintenanceScreen") },
                        modifier = Modifier.weight(0.5f)
                    )
                    Spacer(modifier = Modifier.width(32.dp))  // Space between the two cards
                    MaintenanceCard(
                        title = "MACHINE RUNTIME SECTION",
                        icon = painterResource(R.drawable.machine_logo),
                        arrowIcon = painterResource(R.drawable.btn_image),
                        onArrowClick = { navController.navigate("machineRuntimeScreen") },
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
fun PreviewFunctionMainMenu(){
    val navController= rememberNavController()
    MainMenu(navController = navController)

}

//@Preview
//@Composable
//fun CardPreview(){
//    val navController= rememberNavController()
//    MaintenanceCard("Machine Runtime",
//        painterResource(id=R.drawable.machine_logo),
//        painterResource(id=R.drawable.btn_image),
//        onArrowClick = { navController.navigate("machineRuntimeScreen") },
//    )
//}



@Composable
fun MaintenanceCard(
    title: String,
    icon: Painter,
    arrowIcon: Painter,
    onArrowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    // Animation: initial state is 0.8f, target is 1.0f
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "scaleAnimation"
    )

    // Trigger the animation once when the Composable enters
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
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            Image(
                painter = arrowIcon,
                contentDescription = "Forward",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .clickable { onArrowClick() }
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = icon,
                    contentDescription = "Section Icon",
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = dmRegular,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}










