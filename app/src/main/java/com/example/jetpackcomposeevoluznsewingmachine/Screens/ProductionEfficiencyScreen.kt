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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.ProductionCartItemList
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.WindowInfo
import com.example.jetpackcomposeevoluznsewingmachine.rememberWindowInfo

@Composable
fun ProductionEfficiencyScreen(navController: NavController) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val viewModel: MachineViewModel =viewModel()

    val targetPieces=5000
    val completedPieces=  viewModel.latestPushBackCount.observeAsState(0).value

    val balancePieces= if((targetPieces - completedPieces)>0){
        targetPieces-completedPieces
    }else{ 0 }

    val productionCardListItem=listOf(
        ProductionCartItemList(
            title ="COMPLETED PIECES",
            value ="$completedPieces",
            unit ="" ,
            icon = painterResource(R.drawable.completed_pieces),
            arrowIcon =null,
            onClick = {},
            valueColor = Color(0xFF0BA911)
        ),
        ProductionCartItemList(
            title ="TARGET PIECES",
            value ="$targetPieces",
            unit ="" ,
            icon = painterResource(R.drawable.target_pieces),
            arrowIcon = null,
            onClick = {},
            valueColor = Color(0xFFFC5353)
        ),
        ProductionCartItemList(
            title ="BALANCE PIECES",
            value ="$balancePieces",
            unit ="" ,
            icon = painterResource(R.drawable.balance_pieces),
            arrowIcon = null,
            onClick = {},
            valueColor = Color(0xFF2196F3)
        ))
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
                text = "PRODUCTION EFFICIENCY",
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