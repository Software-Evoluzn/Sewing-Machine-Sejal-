package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeevoluznsewingmachine.MachineViewModel
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
    ShowingCombineGraphs(navController = navController)
}

@Composable
fun ShowingCombineGraphs(navController: NavHostController) {



        ShowingGraphDemo(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(45.dp)
        )

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
                    setDrawGridLines(true)
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
                lineWidth = 5f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawCircles(true)
                circleRadius = 4f
                setCircleColor(Color(0xFF2196F3).toArgb())
            }

            val barDataSet = BarDataSet(barEntries, "PushBackCount").apply {
                color = Color(0xFFE91E63).toArgb()
                setDrawValues(false)

            }
            val barData = BarData(barDataSet).apply {
                barWidth = 0.4f // try values like 0.3f, 0.4f, 0.5f
            }

            val combinedData = CombinedData().apply {
                setData(LineData(lineDataSet))
                setData(barData)
            }

            combinedChart.xAxis.valueFormatter = IndexAxisValueFormatter(reversedSecondData)
            combinedChart.data = combinedData

            // Animate chart (both X and Y over 1000 ms)


            combinedChart.notifyDataSetChanged()
            combinedChart.invalidate()
        }
    )
}






