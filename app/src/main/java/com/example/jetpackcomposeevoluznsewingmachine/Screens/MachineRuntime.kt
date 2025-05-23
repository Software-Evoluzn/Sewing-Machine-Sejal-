package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.PreventiveAndProductionDataClass
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.ProductionCartItemList
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.WindowInfo
import com.example.jetpackcomposeevoluznsewingmachine.rememberWindowInfo


@Composable
fun MachineRuntime(navController: NavController) {
    val viewModel: MachineViewModel = viewModel()
    val latestRunTimeData by viewModel.latestRunTime.observeAsState(0f)
    val latestIdleTime by viewModel.latestIdleTime.observeAsState(0f)
    val latestPushBackCount by viewModel.latestPushBackCount.observeAsState()
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    val productionCardListItem=listOf(
        ProductionCartItemList(
            title ="RUN TIME",
            value ="${String.format("%.2f",latestRunTimeData)}",
            unit ="hrs" ,
            icon = painterResource(R.drawable.clock),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("showCombineGraphScreen")},
            valueColor = Color(0xFFFFC107)
        ),
        ProductionCartItemList(
            title ="IDLE TIME",
            value ="${String.format("%.2f",latestIdleTime)}",
            unit ="hrs" ,
            icon = painterResource(R.drawable.pause),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("showCombineGraphScreen")},
            valueColor = Color(0xFFF44336)
        ),
        ProductionCartItemList(
            title ="PRODUCTION COUNT",
            value ="${latestPushBackCount?:0}",
            unit ="count" ,
            icon = painterResource(R.drawable.counter),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("showCombineGraphScreen")},
            valueColor = Color(0xFF4CAF50)
        ),
        PreventiveAndProductionDataClass(
            title ="PRODUCTION  EFFICIENCY",

            icon = painterResource(R.drawable.efficacy),
            arrowIcon = painterResource(R.drawable.btn_image),
            arrowIconClick = {navController.navigate("productionEfficiency")}

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

        // Logo pinned to top-start
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
                text = "MACHINE RUNTIME",
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
                    itemsIndexed(productionCardListItem) { index,card ->
                        if(index==3 &&  card is PreventiveAndProductionDataClass){
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    itemsIndexed(productionCardListItem) { index,card ->
                        if(index==3 && card is PreventiveAndProductionDataClass) {
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


