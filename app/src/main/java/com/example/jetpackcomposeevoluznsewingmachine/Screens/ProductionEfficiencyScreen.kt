package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.CardItemList
import com.example.jetpackcomposeevoluznsewingmachine.R

@Composable
fun ProductionEfficiencyScreen(navController: NavController) {

    val cardItemList=listOf(
        CardItemList("TARGET PIECES", onCardClick = {navController.navigate("mainMenu")},
            painterResource(R.drawable.production_icon)
        ),
        CardItemList("COMPLETED PIECES", onCardClick = {navController.navigate("mainMenu")},
            painterResource(R.drawable.production_icon)),
        CardItemList("BALANCE PIECES", onCardClick = {navController.navigate("mainMenu")},
            painterResource(R.drawable.production_icon)),

        )
    Column(modifier=Modifier.fillMaxSize().padding(25.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally) {

        val dmRegular = FontFamily(Font(R.font.dmsans_regular))
        Text(
            text = "PRODUCTION EFFICIENCY",
            fontSize = 24.sp,
            fontFamily = dmRegular,
            fontWeight = FontWeight.Bold,
            color= Color(0xFF4B4B4B)
        )



        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
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
                    icon=card.icon

                    )
            }

        }
    }

}