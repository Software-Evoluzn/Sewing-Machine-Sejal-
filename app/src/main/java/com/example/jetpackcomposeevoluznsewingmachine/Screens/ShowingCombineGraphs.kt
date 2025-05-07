package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
  val navController= rememberNavController()
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
    AndroidView(
        modifier = modifier,
        factory = { context ->

            val combinedChart = CombinedChart(context)

            val xLabels = (0..23).map { "${it}h" }

            // Sample data
            val runtimeData = listOf(
                2f, 3f, 4f, 1f, 0f, 2f, 5f, 4f, 3f, 2f, 1f, 2f,
                3f, 4f, 5f, 3f, 2f, 1f, 0f, 1f, 2f, 3f, 4f, 5f
            )
            val idleTimeData = runtimeData.map { runtime ->
                if (runtime == 0f) (1..5).random().toFloat() else 0f
            }
            val pushBackCount = listOf(
                0f, 0f, 2f, 1f, 0f, 2f, 3f, 4f, 0f, 2f, 1f, 0f,
                1f, 0f, 3f, 4f, 5f, 0f, 3f, 2f, 1f, 0f, 1f, 2f
            )

            // Runtime Line
            val runTimeEntries = runtimeData.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }
            val runTimeSet = LineDataSet(runTimeEntries, "Runtime (hrs)").apply {
                color = Color(0xFF2196F3).toArgb() // Blue
                lineWidth = 5f
                setDrawCircles(true)
                setCircleColor(Color(0xFF1976D2).toArgb())
                circleRadius = 4f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawCircleHole(true)
            }

            // Idle Time Line
            val idleTimeEntries = idleTimeData.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }
            val idleTimeSet = LineDataSet(idleTimeEntries, "Idle Time (hrs)").apply {
                color = Color(0xFF4CAF50).toArgb() // Green
                lineWidth = 5f
                setDrawCircles(true)
                setCircleColor(Color(0xFF388E3C).toArgb())
                circleRadius = 4f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawCircleHole(true)
            }

            // Push Back Bar
            val pushBackEntries = pushBackCount.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value)
            }
            val pushBackSet = BarDataSet(pushBackEntries, "PushBack Count").apply {
                color = Color(0xFFFF9800).toArgb() // Orange
                setDrawValues(false)
                barShadowColor = Color.LightGray.toArgb()
            }

            // Combine Line Data
            val lineData = LineData(runTimeSet, idleTimeSet)
            // Combine Bar Data (with narrow bars)
            val barData = BarData(pushBackSet).apply {
                barWidth = 0.5f // thinner bars
            }

            val combinedData = CombinedData().apply {
                setData(lineData)
                setData(barData)
            }

            combinedChart.data = combinedData

            combinedChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(xLabels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textSize = 12f
                labelRotationAngle = -30f
            }
            combinedChart.axisLeft.apply {
                axisMinimum = 0f
                textSize = 12f
                setDrawGridLines(false)
            }
            combinedChart.axisRight.isEnabled = false

            combinedChart.legend.apply {
                isEnabled = true
                textSize = 12f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                yEntrySpace = 10f
            }

            combinedChart.apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                isHighlightFullBarEnabled = false
                animateXY(1200, 1200)
                setExtraOffsets(16f, 24f, 16f, 16f)
            }

            combinedChart.invalidate()
            combinedChart
        }
    )
}




