package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.NotificationAndSoundHelpherClass
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
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TemperatureGraph(
    navController: NavController,
    modifier: Modifier,
    onBack: () -> Unit,
    GraphHeading: String,
    dataLabel: String,
    todayTemps: List<Double>,
    weeklyTemps: List<Double>,
    valueColor: Color,
    unit: String,
    snackBarHostState: SnackbarHostState,
    threshHold: Double,
    shouldTriggerAlert: (Double) -> Boolean,
    alertMessage: String,
    alertTitle: String
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val options = listOf("Today", "Weekly", "Set Range")

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Today") }
    var appliedOption by remember { mutableStateOf("Today") }

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var appliedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var appliedEndDate by remember { mutableStateOf<LocalDate?>(null) }

    val viewModel: MachineViewModel = viewModel()

    val selectedDateRangeDataFlow = remember(appliedStartDate, appliedEndDate) {
        if (appliedStartDate != null && appliedEndDate != null) {
            viewModel.getSelectedDateRangeMaintenance(
                appliedStartDate.toString(),
                appliedEndDate.toString()
            )
        } else {
            flowOf(emptyList())
        }
    }

    val SelectedDateRangeData by selectedDateRangeDataFlow.collectAsState(initial = emptyList())

    val selectedDateHourlyDataFlow = remember(appliedStartDate, appliedEndDate) {
        if (appliedStartDate != null && appliedEndDate != null && appliedStartDate == appliedEndDate) {
            viewModel.getHourlySummaryDateOfSelectedDate(appliedStartDate.toString())
        } else {
            flowOf(emptyList())
        }
    }

    val selectedDateHourlyData by selectedDateHourlyDataFlow.collectAsState(initial = emptyList())

    var hasAlerted by remember { mutableStateOf(false) }
    val sendAlertNotification = NotificationAndSoundHelpherClass()

    LaunchedEffect(Unit) {
        sendAlertNotification.initBuzzerSound(context)
    }

    LaunchedEffect(todayTemps) {
        val latestValue = todayTemps.lastOrNull() ?: return@LaunchedEffect
        if (shouldTriggerAlert(latestValue) && !hasAlerted) {
            sendAlertNotification.NotificationFunction(context, title = alertTitle, message = alertMessage)
            sendAlertNotification.PlayBuzzerSound()
            hasAlerted = true
            snackBarHostState.showSnackbar(alertMessage)
        } else if (!shouldTriggerAlert(latestValue)) {
            hasAlerted = false
        }
    }

    val displayText = when (selectedOption) {
        "Today" -> LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        "Weekly" -> "Weekly"
        "Set Range" -> if (startDate != null && endDate != null) {
            "${startDate!!.format(DateTimeFormatter.ofPattern("dd-MM"))} to ${endDate!!.format(DateTimeFormatter.ofPattern("dd-MM"))}"
        } else "Set Range"
        else -> ""
    }

    val displayBtnText = "Filter"

    // Get proper data based on applied option
    val (xAxisLabels, yAxisData) = when (appliedOption) {
        "Today" -> {
            val hours = (0..23).map { "${it.toString().padStart(2, '0')}:00" }
            val data = if (todayTemps.size == 24) todayTemps else List(24) { 0.0 }
            hours to data
        }

        "Weekly" -> {
            val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val data = if (weeklyTemps.size == 7) weeklyTemps else List(7) { 0.0 }
            days to data
        }

        "Set Range" -> {
            if (appliedStartDate != null && appliedEndDate != null) {
                if (appliedStartDate == appliedEndDate) {
                    val labels = selectedDateHourlyData.map { it.hour }
                    val dataSameDate = when (unit) {
                        "°C" -> selectedDateHourlyData.map { it.avg_temperature }
                        "mm/s" -> selectedDateHourlyData.map { it.avg_vibration }
                        "%" -> selectedDateHourlyData.map { it.avg_oilLevel }
                        else -> emptyList()
                    }
                    labels to dataSameDate
                } else {
                    val formatter = DateTimeFormatter.ofPattern("MM-dd")
                    val labels = SelectedDateRangeData.map {
                        LocalDate.parse(it.day).format(formatter)
                    }
                    val differentDateRange = when (unit) {
                        "°C" -> SelectedDateRangeData.map { it.avg_temperature }
                        "mm/s" -> SelectedDateRangeData.map { it.avg_vibration }
                        "%" -> SelectedDateRangeData.map { it.avg_oilLevel }
                        else -> emptyList()
                    }
                    labels to differentDateRange
                }
            } else emptyList<String>() to emptyList()
        }

        else -> emptyList<String>() to emptyList()
    }


    var isLoading by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }

    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "scale"
    )

    LaunchedEffect(appliedOption, appliedStartDate, appliedEndDate) {
        isLoading = true
        delay(800) // Increased delay for better UX
        isLoading = false
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    if (isLandscape) {
        // Landscape Layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(Color(0xFFF8F9FA))
        ) {
            // Left side - Controls
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = GraphHeading,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = dmRegular,
                        color = Color(0xFF2E3A59)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Options
                Text(
                    text = "Filter:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E3A59)
                )

                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) {
                    Text(selectedOption, color = Color.White, fontSize = 12.sp)
                }

                if (selectedOption == "Set Range") {
                    DatePickerButton(
                        label = "Start",
                        date = startDate,
                        modifier = Modifier.fillMaxWidth()
                    ) { startDate = it }
                    DatePickerButton(
                        label = "End",
                        date = endDate,
                        modifier = Modifier.fillMaxWidth()
                    ) { endDate = it }
                }

                Button(
                    onClick = {
                        appliedOption = selectedOption
                        appliedStartDate = startDate
                        appliedEndDate = endDate
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Apply", color = Color.White, fontSize = 12.sp)
                }

                // Current Selection Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Current:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = displayText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2E3A59)
                        )
                    }
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontSize = 12.sp) },
                            onClick = {
                                selectedOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Right side - Graph
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                if (isLoading) {
                    ShimmerEffect(modifier = Modifier.fillMaxSize().padding(16.dp))
                } else {
                    AnimatedVisibility(
                        visible = yAxisData.isNotEmpty(),
                        enter = fadeIn(tween(800)),
                        exit = fadeOut(tween(400))
                    ) {
                        key(appliedOption + appliedStartDate.toString() + appliedEndDate.toString()) {
                            ShowLineChart(
                                xData = xAxisLabels,
                                yData = yAxisData,
                                dataLabel = dataLabel,
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                valueColor = valueColor,
                                unit = unit,
                                isLandscape = true,
                                context = context
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Portrait Layout (Original with improvements)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFF8F9FA))
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF2E3A59))
                }
                Text(
                    text = GraphHeading,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF2E3A59),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Controls Section
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current: $displayText",
                            color = Color(0xFF2E3A59),
                            fontFamily = dmRegular,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { expanded = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                            ) {
                                Text(displayBtnText, color = Color.White, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    appliedOption = selectedOption
                                    appliedStartDate = startDate
                                    appliedEndDate = endDate
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("Apply", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    if (selectedOption == "Set Range") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DatePickerButton(
                                label = "Start Date",
                                date = startDate,
                                modifier = Modifier.weight(1f)
                            ) { startDate = it }
                            DatePickerButton(
                                label = "End Date",
                                date = endDate,
                                modifier = Modifier.weight(1f)
                            ) { endDate = it }
                        }
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Graph Section
            Card(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                if (isLoading) {
                    ShimmerEffect(modifier = Modifier.fillMaxSize().padding(24.dp))
                } else {
                    AnimatedVisibility(
                        visible = yAxisData.isNotEmpty(),
                        enter = fadeIn(tween(800)),
                        exit = fadeOut(tween(400))
                    ) {
                        key(appliedOption + appliedStartDate.toString() + appliedEndDate.toString()) {
                            ShowLineChart(
                                xData = xAxisLabels,
                                yData = yAxisData,
                                dataLabel = dataLabel,
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                valueColor = valueColor,
                                unit = unit,
                                context = context
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Powered by ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = dmRegular,
                    color = Color(0xFF6B7280)
                )
                Text(
                    "EVOLUZN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF6366F1)
                )
            }
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
    unit: String,
    isLandscape: Boolean = false,
    context: Context
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                setDrawGridBackground(false)
                setDrawBorders(false)
                description.isEnabled = false

                setTouchEnabled(true)
                setDragEnabled(true)
                setScaleEnabled(true)
                setPinchZoom(true)
                setDoubleTapToZoomEnabled(true)

                setVisibleXRangeMaximum(if (isLandscape) 15f else 10f)
                setVisibleXRangeMinimum(3f)
            }
        },
        update = update@{ chart ->
            if (xData.isEmpty() || yData.isEmpty() || xData.size != yData.size) return@update

            val entries = yData.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            val circleColors = yData.map { value ->
                when (unit) {
                    "°C" -> when {
                        value < 45 -> Color(0xFF3B82F6).toArgb()
                        value > 45 -> Color(0xFFEF4444).toArgb()
                        else -> Color(0xFF10B981).toArgb()
                    }

                    "mm/s" -> if (value > 45) Color(0xFFEF4444).toArgb() else Color(0xFF10B981).toArgb()
                    "%" -> if (value < 30) Color(0xFFEF4444).toArgb() else Color(0xFF10B981).toArgb()
                    else -> Color(0xFF10B981).toArgb()
                }
            }

            val dataSet = LineDataSet(entries, dataLabel).apply {
                color = valueColor.toArgb()
                lineWidth = 3f
                circleRadius = 5f
                setDrawCircles(true)
                setDrawValues(false)
                setDrawFilled(true)
                fillColor = valueColor.copy(alpha = 0.3f).toArgb()
                setDrawCircleHole(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                this.circleColors = circleColors
            }

            // Legend
            val legendEntries = when (unit) {
                "°C" -> listOf(
                    LegendEntry("Cold (<25°C)", Legend.LegendForm.CIRCLE, 8f, 2f, null, Color(0xFF3B82F6).toArgb()),
                    LegendEntry("Normal (25-35°C)", Legend.LegendForm.CIRCLE, 8f, 2f, null, Color(0xFF10B981).toArgb()),
                    LegendEntry("Hot (>35°C)", Legend.LegendForm.CIRCLE, 8f, 2f, null, Color(0xFFEF4444).toArgb())
                )

                "mm/s" -> listOf(
                    LegendEntry("Normal", Legend.LegendForm.CIRCLE, 8f, 2f, null, Color(0xFF10B981).toArgb()),
                    LegendEntry("High Vibration", Legend.LegendForm.CIRCLE, 8f, 2f, null, Color(0xFFEF4444).toArgb())
                )

                "%" -> listOf(
                    LegendEntry("Low Oil", Legend.LegendForm.CIRCLE, 8f, 2f, null, Color(0xFFEF4444).toArgb()),
                    LegendEntry("Normal", Legend.LegendForm.CIRCLE, 8f, 2f, null, Color(0xFF10B981).toArgb())
                )

                else -> listOf(
                    LegendEntry("Normal", Legend.LegendForm.CIRCLE, 8f, 2f, null, Color(0xFF10B981).toArgb())
                )
            }

            chart.legend.apply {
                isEnabled = true
                textSize = if (isLandscape) 10f else 11f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setCustom(legendEntries)
                form = Legend.LegendForm.CIRCLE
            }

            chart.data = LineData(dataSet)

            // X Axis
            chart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(xData)
                position = XAxis.XAxisPosition.BOTTOM
                textSize = if (isLandscape) 9f else 10f
                granularity = 1f
                setDrawGridLines(true)
                gridColor = Color(0xFFE5E7EB).toArgb()
                gridLineWidth = 0.5f
                labelRotationAngle = if (isLandscape) -45f else -30f
                setLabelCount(minOf(xData.size, if (isLandscape) 8 else 6), false)
            }

            // Y Axis
            chart.axisLeft.apply {
                textSize = if (isLandscape) 9f else 10f
                setDrawGridLines(true)
                gridColor = Color(0xFFE5E7EB).toArgb()
                gridLineWidth = 0.5f
                setDrawAxisLine(true)
                axisLineColor = Color(0xFF9CA3AF).toArgb()

                val minY = yData.minOrNull() ?: 0.0
                val maxY = yData.maxOrNull() ?: 100.0
                val padding = (maxY - minY) * 0.1
                axisMinimum = maxOf(minY - padding, 0.0).toFloat()
                axisMaximum = (maxY + padding).toFloat()
            }

            chart.axisRight.isEnabled = false

            chart.setExtraOffsets(
                if (isLandscape) 10f else 16f,
                if (isLandscape) 30f else 60f,
                if (isLandscape) 10f else 16f,
                if (isLandscape) 10f else 16f
            )

            // Marker View
            chart.marker = TemperatureMarkerView(context = context, xData, unit).apply {
                chartView = chart
            }

//            chart.animateXY(1000, 1000, Easing.EaseInOutCubic)
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}


@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color(0xFFE5E7EB),
        Color(0xFFF3F4F6),
        Color(0xFFE5E7EB)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnim"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 300f, translateAnim + 300f)
    )

    Spacer(
        modifier = modifier
            .background(brush, shape = RoundedCornerShape(12.dp))
    )
}

// DatePickerButton component (you'll need to implement this based on your date picker)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerButton(
    label: String,
    date: LocalDate?,
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit
) {

    val context= LocalContext.current
    val formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val datePickerDialog = remember{
        DatePickerDialog(context)
    }

    Button(
        onClick = {
            val calendar= Calendar.getInstance()
            datePickerDialog.setOnDateSetListener{_,year,month,day->
                onDateSelected(LocalDate.of(year,month+1,day))
            }
            datePickerDialog.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(
                Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280))
    ) {
        Text(
            text = date?.format(DateTimeFormatter.ofPattern("dd-MM")) ?: label,
            color = Color.White,
            fontSize = 11.sp
        )
    }
}