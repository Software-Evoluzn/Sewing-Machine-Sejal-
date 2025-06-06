package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.WindowInfo
import com.example.jetpackcomposeevoluznsewingmachine.rememberWindowInfo

@Composable
fun BreakDownScreen(navController: NavController) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val windowInfo = rememberWindowInfo()
    if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact){
        //portrait

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with Logo and Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Space for logo - you can add your logo here
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.aquarelle_logo),
                        contentDescription = "logo",
                        modifier = Modifier.size(170.dp).padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "BREAKDOWN IN GARMENT INDUSTRY",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )
            }

            // Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Down Time Card
                item {
                    DownTimeCard()
                }

                // Breakdown Reason Card
                item {
                    BreakdownReasonCard()
                }

                // MTBF Card
                item {
                    MTBFCard()
                }

                // Prediction Card
                item {
                    PredictionCard()
                }

                // MTTR Card
                item {
                    MTTRCard()
                }
            }

            // Footer
            Text(
                text = "Powered by EVOLUZN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color(0xFF666666)
            )
        }
    }

    else{
        //landscape

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with Logo and Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Space for logo - you can add your logo here
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.aquarelle_logo),
                        contentDescription = "logo",
                        modifier = Modifier.size(170.dp).padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "BREAKDOWN IN GARMENT INDUSTRY",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )
            }

            // Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Down Time Card
                item {
                    DownTimeCard()
                }

                // Breakdown Reason Card
                item {
                    BreakdownReasonCard()
                }

                // MTBF Card
                item {
                    MTBFCard()
                }

                // Prediction Card
                item {
                    PredictionCard()
                }

                // MTTR Card
                item {
                    MTTRCard()
                }
            }

            // Footer
            Text(
                text = "Powered by EVOLUZN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color(0xFF666666)
            )
        }
    }


}


@Composable
fun DownTimeCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {



//                new
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Down Time –  % and Min",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
//

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {



//new
                Column {
                    Row {
                        Image(
                            painter = painterResource(R.drawable.percentage_img),
                            contentDescription = "logo",
                            modifier = Modifier.size(50.dp).padding(10.dp)
                        )
                        Text(
                            text = "Percentage of total time:20%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEE0A41),
                            textAlign = TextAlign.Center
                        )


                    }
                    Row {
                        Image(
                            painter = painterResource(R.drawable.actual_clock),
                            contentDescription = "logo",
                            modifier = Modifier.size(50.dp).padding(10.dp)
                        )
                        Text(
                            text = "Actual minutes :120 min",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1FE026),
                            textAlign = TextAlign.Center
                        )


                    }
                }



            }
        }
    }
}













@Composable
fun BreakdownReasonCard() {
    val reasons = listOf(
        "Belt Problem",
        "Components Issue",
        "Machine Head & Feeder Problem",
        "Coil and Water Not Receiving Issue",
        "Electrical & Mechanical Issue",
        "Not Lifting and Oil Leaking",
        "Sensor and Mechanism Adjust Issue"
    )
    val checkedStates = remember { mutableStateListOf(*Array(reasons.size) { false }) }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Breakdown Reason",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Scrollable list of reasons
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(reasons) { index, reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = checkedStates[index],
                            onCheckedChange = { checkedStates[index] = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF4299E1),
                                uncheckedColor = Color(0xFFE0E0E0)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reason,
                            fontSize = 12.sp,
                            color = Color(0xFF333333),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Submit Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4299E1), RoundedCornerShape(6.dp))
                    .clickable {
                        val selectedReasons = reasons.filterIndexed { index, _ -> checkedStates[index] }
                        Log.d("BreakdownReasonCard", "Selected Reasons: $selectedReasons")
                        // TODO: Replace with actual submit logic like sending to ViewModel or API
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Submit",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun MTBFCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {



//                new
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "MTBF",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
//

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {



//new
                Row {
                    Image(
                        painter = painterResource(R.drawable.actual_clock),
                        contentDescription = "logo",
                        modifier = Modifier.size(50.dp).padding(10.dp)
                    )
                    Text(
                        text = "Mean time to breakdown failure:480 min",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1836D9),
                        textAlign = TextAlign.Center
                    )


                }


            }
        }
    }
}

@Composable
fun PredictionCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {



//                new
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Prediction Of Breakdown",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
//

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {



//new
                Row {
                    Image(
                        painter = painterResource(R.drawable.breakdown_expected_img),
                        contentDescription = "logo",
                        modifier = Modifier.size(50.dp).padding(10.dp)
                    )
                    Text(
                        text = "Next Breakdown Expected In:3 days",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE59218),
                        textAlign = TextAlign.Center
                    )


                }


            }
        }
    }
}




@Composable
fun MTTRCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {



//                new
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "MTTR",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
//

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {








//new
                Row {
                    Image(
                        painter = painterResource(R.drawable.actual_clock),
                        contentDescription = "logo",
                        modifier = Modifier.size(50.dp).padding(10.dp)
                    )
                    Text(
                        text = "Mean Time to Repair:3 days",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E9612),
                        textAlign = TextAlign.Center
                    )


                }


            }
        }
    }
}
