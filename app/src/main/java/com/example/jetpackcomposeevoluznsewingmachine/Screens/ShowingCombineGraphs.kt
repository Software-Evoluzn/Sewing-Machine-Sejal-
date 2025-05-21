package com.example.jetpackcomposeevoluznsewingmachine.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate
import kotlin.math.ceil


@Preview(showBackground = true)
@Composable
fun showPreview(){
    val navController= rememberNavController()
//    ShowingCombineGraphs(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowingCombineGraphs(navController: NavController, onBack: () -> Unit, GraphHeading: String) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    var selectedOption by remember{mutableStateOf("Today")}
    val options=listOf("Today","Weekly","Set Range")

    var selectedHour by remember{mutableStateOf("Select Hour")}
    val hourOptions =(0..23).map{"$it:00-${it+1}:00"}

    var expandedMain by remember{mutableStateOf(false)}
    var expandedHour by remember{mutableStateOf(false)}

    var startDate by remember{ mutableStateOf<LocalDate?>(null)}
    var endDate by remember {mutableStateOf<LocalDate?>(null)}
    val context=LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color=Color(0xFFF3F0F0))

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
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 50.dp)
            )
            Box(modifier=Modifier.align(Alignment.BottomEnd)){
                ExposedDropdownMenuBox(
                    expanded = expandedMain,
                    onExpandedChange = { expandedMain = !expandedMain}
                ) {
                    TextField(
                        value=selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        label={Text("Set Date")},
                        trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMain)},
                        modifier=Modifier.menuAnchor()

                    )
                    DropdownMenu(expanded=expandedMain,
                        onDismissRequest = { expandedMain=false }){
                        options.forEach{option->
                            DropdownMenuItem(
                                    text={ Text(option)},
                                   onClick = {
                                       selectedOption=option
                                       expandedMain=false
                                   }
                            )
                        }

                    }
                    Spacer(modifier=Modifier.height(16.dp))

                    when(selectedOption){
                        "Today" ->{
                             ExposedDropdownMenuBox(
                                 expanded=expandedHour,
                                 onExpandedChange = {expandedHour = !expandedHour}
                             ) {
                                 TextField(
                                     value=selectedHour,
                                     onValueChange={},
                                     readOnly=true,
                                     label={Text("Hour")},
                                     trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(expanded=expandedHour)},
                                     modifier=Modifier.menuAnchor()
                                 )

                                 DropdownMenu(
                                     expanded=expandedHour,
                                     onDismissRequest={expandedHour=false}
                                 ){
                                     hourOptions.forEach{hour->
                                              DropdownMenuItem(
                                                  text = {Text(hour)},
                                                  onClick = {
                                                      selectedHour=hour
                                                      expandedHour=false

                                                  }

                                              )

                                     }

                                 }
                             }

                        }
                        "Weekly" ->{

                        }
                        "Set Range" ->{

                        }

                    }






                }

            }



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
    val hourlyData by viewModel.getCombineGraphOfTodayData.collectAsState(emptyList())

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
                    setCenterAxisLabels(true)
                }

                axisLeft.apply {
                    axisMinimum = 0f
                    textSize = 12f
                    setDrawGridLines(false)
                }

                axisRight.apply {
                    isEnabled = true
                    axisMinimum = 0f
                    axisMaximum = 100f // Percentage for cycle count
                    textSize = 12f
                    textColor = Color(0xFF2196F3).toArgb()
                }

                legend.apply {
                    isEnabled = true
                    textSize = 12f
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    yEntrySpace = 10f
                    xEntrySpace = 20f
                }
            }
        },
        update = update@{ combinedChart ->

            if (hourlyData.isEmpty()) return@update

            val maxCyclePossible = 100f // Replace this with your actual hourly max if dynamic

            // --- Bar Entries ---
            val barEntriesRunTime = hourlyData.mapIndexed { index, data ->
                BarEntry(index.toFloat(), data.total_runtime.toFloat())
            }
            val barEntriesIdleTime = hourlyData.mapIndexed { index, data ->
                BarEntry(index.toFloat(), data.total_idletime.toFloat())
            }

            // --- Line Entries: Convert cycle count to percentage ---
            val lineEntriesCycleCount = hourlyData.mapIndexed { index, data ->
                val percentage = (data.cycle_count * 100f) / maxCyclePossible
                Entry(index.toFloat(), percentage)
            }

            val runTimeDataSet = BarDataSet(barEntriesRunTime, "Run Time").apply {
                color = Color(0xFFE91E63).toArgb()
                setDrawValues(false)
            }

            val idleTimeDataSet = BarDataSet(barEntriesIdleTime, "Idle Time").apply {
                color = Color(0xFFFFC107).toArgb()
                setDrawValues(false)
            }

            val lineDataSet = LineDataSet(lineEntriesCycleCount, "% Cycle Count").apply {
                axisDependency = YAxis.AxisDependency.RIGHT
                color = Color(0xFF2196F3).toArgb()
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(Color(0xFF2196F3).toArgb())
                setDrawValues(false)
                valueTextSize = 10f
            }

            val barData = BarData(runTimeDataSet, idleTimeDataSet).apply {
                barWidth = 0.3f
            }

            val lineData = LineData(lineDataSet)

            val groupSpace = 0.2f
            val barSpace = 0f
            barData.groupBars(0f, groupSpace, barSpace)

            combinedChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(hourlyData.map { it.hour })
                axisMinimum = 0f
                axisMaximum = barData.getGroupWidth(groupSpace, barSpace) * hourlyData.size
                labelRotationAngle = -45f
            }

            // Max Y for bars (left axis)
            val maxBarY = hourlyData.maxOf { it.total_runtime + it.total_idletime }.toFloat()
            val roundedMax = ceil((maxBarY * 1.2f) / 10) * 10

            combinedChart.axisLeft.axisMaximum = roundedMax

            val combinedData = CombinedData().apply {
                setData(barData)
                setData(lineData)
            }

            combinedChart.data = combinedData
            combinedChart.notifyDataSetChanged()
            combinedChart.invalidate()
        }
    )
}








