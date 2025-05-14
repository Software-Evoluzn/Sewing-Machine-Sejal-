package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.CardItemList
import com.example.jetpackcomposeevoluznsewingmachine.R


@Composable
fun DashBoardLiveScreen(navController: NavController){
   val dmRegular= FontFamily(Font(R.font.dmsans_regular))

    Box(
        modifier=Modifier.fillMaxSize()){


            // Arrow at top-right
            Image(
                painter = painterResource(R.drawable.download_img),
                contentDescription = "Download",
                modifier = Modifier
                    .align(Alignment.TopEnd)

                    .size(37.dp)
                    .clickable {  }

            )

        Column(modifier=Modifier.padding(25.dp),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){

            Text(text="SEWING MACHINE LIVE DASHBOARD",
                fontSize = 24.sp,
                fontFamily = dmRegular,
                fontWeight = FontWeight.Bold,
                color=Color(0xFF4B4B4B)

            )


            DashBoardScreen(navController = navController)

        }


    }


}


@Composable
fun DashBoardScreen(navController: NavController) {
    val cardItemList=listOf(
        CardItemList("Production", onCardClick = {navController.navigate("mainMenu")}),
        CardItemList("Production", onCardClick = {navController.navigate("mainMenu")}),
        CardItemList("Production", onCardClick = {navController.navigate("mainMenu")}),
        CardItemList("Production", onCardClick = {navController.navigate("mainMenu")}),
        CardItemList("Production", onCardClick = {navController.navigate("mainMenu")}),
        CardItemList("Production", onCardClick = {navController.navigate("mainMenu")}),
        CardItemList("Production", onCardClick = {navController.navigate("mainMenu")}),
        CardItemList("Production", onCardClick = {navController.navigate("mainMenu")}),
        )

    LazyVerticalGrid(
        columns=GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)


    ) {
        items(cardItemList) { card ->
            ShowingCard(
                title = card.title,
                onCardClick = card.onCardClick,

            )
        }

    }


}

@Composable
fun ShowingCard(
    title: String,
    modifier:Modifier=Modifier,
    onCardClick : ()->Unit
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
            .clickable {  onCardClick() }
            .padding(8.dp)
            .defaultMinSize(minWidth = 80.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


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

