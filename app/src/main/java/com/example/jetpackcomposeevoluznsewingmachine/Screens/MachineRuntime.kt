package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.ProductionCartItemList
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.WindowInfo
import com.example.jetpackcomposeevoluznsewingmachine.rememberWindowInfo


@Composable
fun MachineRuntime(navController: NavController) {
    val viewModel: MachineViewModel = viewModel()
    val latestRunTimeData by viewModel.latestRunTime.observeAsState(0f)
    val latestIdleTime by viewModel.latestIdleTime.observeAsState(0f)
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    val productionCardListItem=listOf(
        ProductionCartItemList(
            title ="RUN TIME",
            value ="${String.format("%.2f",latestRunTimeData)}",
            unit ="hrs" ,
            icon = painterResource(R.drawable.run_time),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("showCombineGraphScreen")},
            valueColor = Color(0xFF3386FF)
        ),
        ProductionCartItemList(
            title ="IDLE TIME",
            value ="${String.format("%.2f",latestIdleTime)}",
            unit ="hrs" ,
            icon = painterResource(R.drawable.idle_time),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("showCombineGraphScreen")},
            valueColor = Color(0xFF8569D8)
        ),
        ProductionCartItemList(
            title ="PRODUCTION COUNT",
            value ="${String.format("%.2f",latestIdleTime)}",
            unit ="hrs" ,
            icon = painterResource(R.drawable.run_time),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("showCombineGraphScreen")},
            valueColor = Color(0xFF3386FF)
        ),
        ProductionCartItemList(
            title ="PRODUCTION  EFFICIENCY",
            value ="${String.format("%.2f",latestIdleTime)}",
            unit ="hrs" ,
            icon = painterResource(R.drawable.run_time),
            arrowIcon = painterResource(R.drawable.btn_image),
            onClick = {navController.navigate("productionEfficiency")},
            valueColor = Color(0xFF3386FF)
        )

    )

    val windowInfo = rememberWindowInfo()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(color=Color(0xFFF3F0F0)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "MACHINE RUNTIME",
            fontSize = 24.sp,
            fontFamily = dmRegular,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B)
        )
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

@Preview
@Composable
fun PreviewFunctionMachineRuntime(){
    val navController= rememberNavController()
    MachineRuntime(navController)
}