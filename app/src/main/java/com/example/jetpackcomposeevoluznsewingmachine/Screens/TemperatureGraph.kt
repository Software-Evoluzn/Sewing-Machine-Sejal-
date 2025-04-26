package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.TemperatureMarkerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


@Composable
fun TemperatureGraph(navController: NavController,modifier: Modifier, onBack: () -> Unit,GraphHeading: String) {
    // Remember scroll state
    val scrollState = rememberScrollState()
    val viewModel: MachineViewModel = viewModel()
    val trend by viewModel.temperatureTrend.observeAsState()

    val options =listOf("Today","Weekly","Set Range")
    var expanded by remember{ mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    var startAnimation by remember { mutableStateOf(false) }
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "parameterBoxScale"
    )
    LaunchedEffect(Unit) {
        startAnimation = true
        viewModel.fetch7DayTemperatureTrend()
    }
    // Debug: Log the data in trend
    println("TemperatureGraphTrend : $trend")
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

                Column(horizontalAlignment = Alignment.End) {
                    Row {


                    Text(text = selectedOption ?: "", color = Color.Black,
                        modifier=modifier.padding(top=12.dp),
                        fontFamily = dmRegular, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(12.dp), // Rounded corners with 12.dp radius
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp, // Default shadow elevation
                            pressedElevation = 8.dp, // Elevation when pressed
                            disabledElevation = 0.dp // No elevation when disabled
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF492883) // Custom background color
                        )

                    ) {
                        Text(
                            text = "Set Date",
                            color = Color.White
                        )
                    }
                }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                    // Handle the selection (e.g., update UI or perform an action)
                                }
                            )
                        }

                    }
                }

            }


            Card(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .weight(2.8f)
                    .fillMaxHeight()
                    ,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                val x_axis_values = listOf("sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat")
                trend?.let {
                    ShowLineChart(
                        xData = x_axis_values,
                        yData = it,
                        dataLabel = "Temperature Graph",
                        modifier = Modifier.fillMaxSize().padding(35.dp))
                }
            }
        }
        Spacer(modifier=Modifier.height(25.dp))
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

@Composable
fun ShowLineChart(
    xData: List<String>,
    yData: List<Double>,
    dataLabel: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val chart = LineChart(context)

            // All entries for one smooth line
            val entries = yData.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            val circleColors = yData.map { value ->
                when {
                    value < 60 -> Color(0xFF4CAF50).toArgb()     // Green (Normal)
                    value in 60.0..75.0 -> Color(0xFFFF9800).toArgb() // Orange (Warning)
                    else -> Color(0xFFF44336).toArgb()           // Red (Critical)
                }
            }



            // One dataset for smooth line
            val dataSet = LineDataSet(entries, dataLabel).apply {
                color = Color(0xFFEE5D50).toArgb() // Line color (blue or any base)
                lineWidth = 2.5f
                circleRadius = 6f
                valueTextSize = 0f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(false)
                setDrawValues(false)
                setDrawCircleHole(true)


            }
            dataSet.circleColors = circleColors
            dataSet.setDrawCircleHole(false)

            val legend = chart.legend
            legend.isEnabled = true
            legend.textSize = 12f
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setCustom(
                listOf(
                    LegendEntry("Normal", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color(0xFF4CAF50).toArgb()),
                    LegendEntry("Warning", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color(0xFFFF9800).toArgb()),
                    LegendEntry("Critical", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color(0xFFF44336).toArgb())
                )
            )


            chart.data = LineData(dataSet)





            // Chart styling
            chart.description.isEnabled = false
            chart.legend.isEnabled = true
            chart.legend.textSize = 12f
            chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            chart.legend.orientation = Legend.LegendOrientation.HORIZONTAL

            val xAxis = chart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xData)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textSize = 12f
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.labelRotationAngle = -20f

            chart.axisLeft.textSize = 12f
            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.setDrawAxisLine(true)
            chart.axisRight.isEnabled = false

            chart.setExtraOffsets(16f, 16f, 16f, 16f)
            chart.setTouchEnabled(true)
            chart.setScaleEnabled(false)
            chart.setPinchZoom(false)
            chart.animateXY(800, 800, Easing.EaseInOutQuad)

            val markerView = TemperatureMarkerView(context, xData)
            markerView.chartView = chart
            chart.marker = markerView

            chart.invalidate()
            chart
        }
    )
}































