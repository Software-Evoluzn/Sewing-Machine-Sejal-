package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.key

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
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
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TemperatureGraph(navController: NavController,
                     modifier: Modifier,
                     onBack: () -> Unit,
                     GraphHeading: String,
                     dataLabel:String,
                     todayTemps: List<Double>,
                     weeklyTemps: List<Double>,
                     valueColor: Color,
                     unit:String) {




    val options =listOf("Today","Weekly")
    var expanded by remember{ mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Weekly") }

    val displayText = when(selectedOption){
        "Today" -> {
            val today=LocalDate.now()
            today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
        "Weekly" -> "Weekly"
        else -> "Weekly"
    }

    val displayBtnText=when(selectedOption){
         "Today" -> { "Today"}
        "Weekly" -> {"Weekly"}
        else -> "Weekly"
    }


    val (xAxisLabels, yAxisData) = when (selectedOption) {
        "Today" -> {
            val hours = (0..23).map { hour ->
                "${hour.toString().padStart(2, '0')}:00" // Show all hours
            }
            // Y Axis Data - Always 24 values (default 0.0)
            val fullTodayTemps = todayTemps.takeIf { it.size == 24 } ?: List(24) { 0.0 }

            hours to fullTodayTemps
        }
        "Weekly" -> {
            val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            days to weeklyTemps
        }

        else -> {
            val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            days to weeklyTemps
        }
    }

    var startAnimation by remember { mutableStateOf(false) }
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "parameterBoxScale"
    )
    LaunchedEffect(Unit) {
        startAnimation = true

    }

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
                        Text(text = displayText, color = Color.Black,
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
                            text = displayBtnText,
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

                                }
                            )
                        }

                    }
                }

            }


            var isLoading by remember { mutableStateOf(false) }

            LaunchedEffect(selectedOption) {
                isLoading = true
                delay(500) // Show shimmer for 500ms
                isLoading = false
            }

            Card(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .weight(2.8f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                if (isLoading) {
                    // Show shimmer while loading
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(35.dp)
                    )
                } else {
                    // Show graph with animation
                    AnimatedVisibility(
                        visible = yAxisData.isNotEmpty(),
                        enter = fadeIn(animationSpec = tween(600)),
                        exit = fadeOut(animationSpec = tween(600))
                    ) {
                        key(selectedOption) {
                            ShowLineChart(
                                xData = xAxisLabels,
                                yData = yAxisData,
                                dataLabel = dataLabel,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(35.dp),
                                valueColor = valueColor,
                                unit

                            )
                        }
                    }
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
    modifier: Modifier = Modifier,
    valueColor: Color,
    unit:String

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
                color = valueColor.toArgb() // Line color (blue or any base)
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
            xAxis.setLabelCount(xData.size, true) // <-- force label count

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

// Add padding to avoid curve cutting at top
            val maxY = yData.maxOrNull() ?: 0.0
            chart.axisLeft.axisMaximum = (maxY + 10f).toFloat()

            val markerView = TemperatureMarkerView(context, xData,unit)
            markerView.chartView = chart
            chart.marker = markerView

            chart.invalidate()
            chart
        }
    )
}





@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnim"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 200f, translateAnim + 200f)
    )

    Spacer(
        modifier = modifier
            .background(brush, shape = RoundedCornerShape(12.dp))
    )
}





























