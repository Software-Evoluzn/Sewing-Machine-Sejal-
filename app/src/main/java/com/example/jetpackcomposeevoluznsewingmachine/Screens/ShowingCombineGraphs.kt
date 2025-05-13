package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeevoluznsewingmachine.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


@Preview(showBackground = true)
@Composable
fun showPreview(){
    val navController= rememberNavController()
//    ShowingCombineGraphs(navController = navController)
}

@Composable
fun ShowingCombineGraphs(navController: NavController, onBack: () -> Unit, GraphHeading: String) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).background(color=Color(0xFFF3F0F0))

    ) {
        // Top Bar with Back Button and Heading
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = GraphHeading,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmRegular,
                color = Color(0xFF4B4B4B),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Card(
                modifier = Modifier
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                    ShowingGraphDemo(
                            navController = navController,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(35.dp)

                            )
                     }
                }
            }

         }

}

@Composable
fun ShowingGraphDemo(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel: MachineViewModel = viewModel()

    val runTimeData by viewModel.realTimeRunTimeData.collectAsState(emptyList())
    val secondData by viewModel.realTimeSecond.collectAsState(emptyList())
    val pushBackCount by viewModel.realTimePushBackCount.collectAsState(emptyList())

    val reversedRunTimeData = runTimeData.asReversed()
    val reversedSecondData = secondData.asReversed()
    val reversedPushBackCount = pushBackCount.asReversed()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            CombinedChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                isHighlightFullBarEnabled = false
                drawOrder = arrayOf(
                    CombinedChart.DrawOrder.BAR,
                    CombinedChart.DrawOrder.LINE
                )
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    textSize = 12f
                    labelRotationAngle = -30f
                }
                axisLeft.apply {
                    axisMinimum = 0f
                    textSize = 12f
                    setDrawGridLines(false)
                }
                axisRight.isEnabled = false
                legend.apply {
                    isEnabled = true
                    textSize = 12f
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    yEntrySpace = 10f
                }
            }
        },
        update = update@{ combinedChart ->
            if (reversedRunTimeData.isEmpty() || reversedSecondData.isEmpty() || reversedPushBackCount.isEmpty()) {
                return@update
            }

            val lineEntries = reversedRunTimeData.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }
            val barEntries = reversedPushBackCount.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value.toFloat())
            }

            val lineDataSet = LineDataSet(lineEntries, "Runtime (hrs)").apply {
                color = Color(0xFF2196F3).toArgb()
                lineWidth = 3f
                setDrawValues(false)
                setDrawCircles(false)
                mode = LineDataSet.Mode.STEPPED

            }

            val barDataSet = BarDataSet(barEntries, "PushBackCount").apply {
                color = Color(0xFFE91E63).toArgb()
                setDrawValues(false)

            }
            val barData = BarData(barDataSet).apply {
                barWidth = 0.1f // try values like 0.3f, 0.4f, 0.5f
            }

            val combinedData = CombinedData().apply {
                setData(LineData(lineDataSet))
                setData(barData)
            }

            combinedChart.xAxis.valueFormatter = IndexAxisValueFormatter(reversedSecondData)
            combinedChart.data = combinedData

            // Animate chart (both X and Y over 1000 ms)

            val maxDataValue = (reversedPushBackCount.maxOrNull()?:0f).toFloat()
            val dynamicMaxValue = (maxDataValue+ 2).toInt() // Increase by 5 units

            // Set dynamic max value for Y-axis
            combinedChart.axisLeft.axisMaximum = dynamicMaxValue.toFloat()


            combinedChart.notifyDataSetChanged()
            combinedChart.invalidate()
        }
    )
}






