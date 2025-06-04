package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.CustomMarkView
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.GraphDataModel
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
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import kotlin.math.ceil

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowingCombineGraphs(navController: NavController, onBack: () -> Unit, GraphHeading: String) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    val viewModel: MachineViewModel =viewModel()

    var selectedOption by remember{mutableStateOf("Today")}
    val options=listOf("Today","Weekly","Set Range")

    var selectedHour by remember{mutableStateOf("Select Hour")}
    val hourOptions =(0..23).map{"$it:00-${it+1}:00"}

    var expandedMain by remember{mutableStateOf(false)}
    var expandedHour by remember{mutableStateOf(false)}

    var startDate by remember{ mutableStateOf<LocalDate?>(null)}
    var endDate by remember {mutableStateOf<LocalDate?>(null)}

    val todayCombineGraph by viewModel.getCombineGraphOfTodayData.collectAsState(emptyList())
    val todayIndividualHourCombineGraph by viewModel.
    getIndividualHourCombineGraphData(selectedHour).collectAsState(emptyList())

    val setRangeCombineGraphShowing by
    if(startDate != null  && endDate != null) {
        viewModel.getSetRangeCombineGraphShow(
            startDate.toString(),
            endDate.toString()
        ).collectAsState(emptyList())
    }else{
        remember { mutableStateOf(emptyList()) }
    }

    val setRangeSameDateCombineGraph by
    if(startDate != null) {
        viewModel.getSameDateCombineGraph(startDate.toString()).collectAsState(emptyList())
    }else{
        remember { mutableStateOf(emptyList()) }
    }

    val setHourOfSameDateCombineGraph by viewModel.
    getSameDateHourDataCombineGraph(startDate.toString(),selectedHour).collectAsState(emptyList())

    val setWeeklyCombineGraph by viewModel.getWeeklyCombinedGraph().collectAsState(emptyList())




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
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
            }




        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally) ,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Date Option Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedMain,
                onExpandedChange = { expandedMain = !expandedMain }
            ) {
                TextField(
                    value = selectedOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Set Date") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMain)

                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        unfocusedIndicatorColor  = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .width(150.dp)
                        .height(55.dp)
                        .border(0.1.dp,color=Color.LightGray, RoundedCornerShape(8.dp))

                )
                ExposedDropdownMenu(
                    expanded = expandedMain,
                    onDismissRequest = { expandedMain = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOption = option
                                expandedMain = false
                                selectedHour = "Select Hour"
                                startDate = null
                                endDate = null
                            }
                        )
                    }
                }
            }

            // Hour Dropdown
//            if (selectedOption == "Today" || (selectedOption == "Set Range" && startDate == endDate && startDate != null)) {
//                ExposedDropdownMenuBox(
//                    expanded = expandedHour,
//                    onExpandedChange = { expandedHour = !expandedHour }
//                ) {
//                    TextField(
//                        value = selectedHour,
//                        onValueChange = {},
//                        readOnly = true,
//                        label = { Text("Select Hour") },
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHour)
//                        },
//                        colors = TextFieldDefaults.textFieldColors(
//                            containerColor = Color.White,
//                            unfocusedIndicatorColor  = Color.Transparent,
//                            focusedIndicatorColor = Color.Transparent,
//                            disabledIndicatorColor = Color.Transparent
//                        ),
//                        modifier = Modifier
//                            .menuAnchor()
//                            .width(170.dp)
//                            .height(55.dp)
//                            .border(0.1.dp,color=Color.LightGray, RoundedCornerShape(8.dp))
//                    )
//                    ExposedDropdownMenu(
//                        expanded = expandedHour,
//                        onDismissRequest = { expandedHour = false }
//                    ) {
//                        hourOptions.forEach { hour ->
//                            DropdownMenuItem(
//                                text = { Text(hour) },
//                                onClick = {
//                                    selectedHour = hour
//                                    expandedHour = false
//                                }
//                            )
//                        }
//                    }
//                }
//            }


            if (selectedOption == "Set Range") {
                DatePickerButton("Start Date",startDate){selected->
                    startDate=selected

                }
                DatePickerButton("End Date",endDate){selected->
                    endDate=selected

                }


            }

            if(selectedOption == "Weekly"){
               Text("weekly")
            }
        }






        val graphToShowData =when (selectedOption) {
            "Today" -> {

                    todayCombineGraph.map {
                        GraphDataModel(it.hour, it.total_runtime, it.total_idleTime, it.cycle_count,labelType = "hour")

                    }
            }

            "Weekly" -> {
                setWeeklyCombineGraph.map {
                    GraphDataModel(it.day, it.total_runtime, it.total_idleTime, it.cycle_count,labelType = "Day")
                }

            }

            "Set Range" -> {
                if (startDate != null && endDate != null) {
                    if (startDate == endDate) {


                            setRangeSameDateCombineGraph.map {
                                GraphDataModel(
                                    it.hour,
                                    it.total_runtime,
                                    it.total_idleTime,
                                    it.cycle_count,
                                            labelType = "hour"
                                )
                            }


                    } else {

                       setRangeCombineGraphShowing.map{
                            GraphDataModel(it.day,it.total_runtime,it.total_idleTime,it.cycle_count,labelType = "Date")
                        }
                    }
                } else {
                    emptyList()
                }


            }

            else -> {
                todayCombineGraph.map {
                    GraphDataModel(it.hour, it.total_runtime, it.total_idleTime, it.cycle_count)

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
                    if(graphToShowData.isEmpty()){
                        Text(
                            text = "No data available for selected range/hour.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                    }else{

                        ShowingGraphDemo(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(35.dp),
                            data = graphToShowData as List<GraphDataModel>

                        )

                    }

                     }
                }
            }

         }




}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerButton(label:String,date:LocalDate?,onDateSelected:(LocalDate)->Unit){
    val context= LocalContext.current
    val formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val datePickerDialog = remember{
        DatePickerDialog(context)
    }

    Button(onClick = {
        val calendar=Calendar.getInstance()
        datePickerDialog.setOnDateSetListener{_,year,month,day->
            onDateSelected(LocalDate.of(year,month+1,day))
        }
        datePickerDialog.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    },
        colors=ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border= BorderStroke(0.1.dp,Color.LightGray),
        shape= RoundedCornerShape(8.dp)

    )
    {
        Text(text="$label : ${date?.format(formatter)?:"Select"}")

    }




}



@Composable
fun ShowingGraphDemo(navController: NavController, modifier: Modifier = Modifier,
                     data: List<GraphDataModel>) {
    val viewModel: MachineViewModel = viewModel()
    val hourlyData =data
    println(hourlyData)


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
                    granularity=1f

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


            // --- Bar Entries ---
            val barEntriesRunTime = hourlyData.mapIndexed { index, data ->
                BarEntry(index.toFloat(), data.runTime.toFloat()/3600f)
            }
            val barEntriesIdleTime = hourlyData.mapIndexed { index, data ->
                BarEntry(index.toFloat(), data.idleTime.toFloat()/3600f)
            }

            // --- Line Entries: Convert cycle count to percentage ---
            val lineEntriesCycleCount = hourlyData.mapIndexed { index, data ->
                Entry(index.toFloat(), data.cycleCount.toFloat())
            }

            val runTimeDataSet = BarDataSet(barEntriesRunTime, "Run Time (hrs)").apply {
                color = Color(0xFFE91E63).toArgb()
                setDrawValues(false)
                axisDependency=YAxis.AxisDependency.RIGHT
            }

            val idleTimeDataSet = BarDataSet(barEntriesIdleTime, "Idle Time (hrs)").apply {
                color = Color(0xFFFFC107).toArgb()
                setDrawValues(false)
                axisDependency=YAxis.AxisDependency.RIGHT
            }

            val lineDataSet = LineDataSet(lineEntriesCycleCount, "Cycle Count").apply {
                axisDependency = YAxis.AxisDependency.LEFT
                color = Color(0xFF2196F3).toArgb()
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(Color(0xFF2196F3).toArgb())
                setDrawValues(false)
                valueTextSize = 10f
                valueFormatter=object:ValueFormatter(){
                    override fun getPointLabel(entry: Entry?): String {
                        return entry?.y?.toInt()?.toString() ?: ""
                    }
                }
            }

            val barData = BarData(runTimeDataSet, idleTimeDataSet).apply {
                barWidth = 0.3f
            }

            val lineData = LineData(lineDataSet)

            val groupSpace = 0.2f
            val barSpace = 0f
            barData.groupBars(0f, groupSpace, barSpace)



            combinedChart.xAxis.apply {
                val xLabels = hourlyData.map { it.xLabel }
                valueFormatter = IndexAxisValueFormatter(xLabels)
                granularity = 1f
                setDrawGridLines(false)
                labelRotationAngle = -30f
                setCenterAxisLabels(true)

                when (hourlyData.firstOrNull()?.labelType) {
                    "hour" -> {
                        axisMinimum = 0f
                        axisMaximum = 24f  // Show 24 hours
                        labelCount = 24
                    }

                    "Day" -> {
                        axisMinimum = 0f
                        axisMaximum = 7f   // 7 days
                        labelCount = xLabels.size
                    }

                    "Date" -> {
                        axisMinimum = 0f
                        axisMaximum = xLabels.size.toFloat()
                        labelCount = xLabels.size
                    }

                    else -> {
                        axisMinimum = 0f
                        axisMaximum = xLabels.size.toFloat()
                        labelCount = xLabels.size
                    }
                }
            }


            // Max Y for bars (left axis)
            val maxRunIdle = hourlyData.maxOf { (it.runTime + it.idleTime) / 3600f }
            val maxCycle = hourlyData.maxOf { it.cycleCount.toFloat() }
            val maxLeftAxis = ceil(maxOf(maxRunIdle.toFloat(), maxCycle) * 1.2f)
            combinedChart.axisLeft.axisMaximum = maxLeftAxis.toFloat()




            combinedChart.axisRight.apply {
                isEnabled = true
                setDrawGridLines(false)
                axisMinimum = 0f
                granularity = 1f
                textSize = 12f
                axisMaximum = ceil(
                    hourlyData.maxOf { (it.runTime + it.idleTime) / 3600f } * 1.2f // assuming values in hrs
                ).toFloat()
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()} hr"
                    }
                }
            }



            val combinedData = CombinedData().apply {
                setData(barData)
                setData(lineData)
            }

            val marker=CustomMarkView(
                context = combinedChart.context,
                layoutResource = R.layout.custom_markview,
                hourlyData = hourlyData
            )
            marker.chartView = combinedChart
            combinedChart.marker = marker

//            combinedChart.animateXY(1000, 1000)

            combinedChart.data = combinedData
            combinedChart.notifyDataSetChanged()
            combinedChart.invalidate()
        }
    )
}








