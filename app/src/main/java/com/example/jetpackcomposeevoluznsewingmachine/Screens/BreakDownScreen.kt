package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass
import com.example.jetpackcomposeevoluznsewingmachine.R
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.BreakDownViewModel
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.MissingDataLogViewModel
import com.example.jetpackcomposeevoluznsewingmachine.WindowInfo
import com.example.jetpackcomposeevoluznsewingmachine.rememberWindowInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.ZoneId




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BreakDownScreen(navController: NavController) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val viewModel: MissingDataLogViewModel = viewModel()
    val totalDowntime by viewModel.totalDowntime.collectAsState()
    val mttr by viewModel.mttr.collectAsState()
    val mtbf by viewModel.mtbf.collectAsState()
    val predictionText by viewModel.predictionText.collectAsState()

    LaunchedEffect(Unit) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val today = LocalDate.now()
        val startDateTime = LocalDateTime.of(today, LocalTime.of(10, 0)) // 10:00 AM today
        val endDateTime = LocalDateTime.of(today, LocalTime.of(18, 0)) // 8:00 PM today

        viewModel.computeMetrics(
            start = startDateTime.format(formatter),
            end = endDateTime.format(formatter)
        )
    }


    // load in ViewModel
    val context = LocalContext.current
    val windowInfo = rememberWindowInfo()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv"))
        { uri ->
            uri?.let {
                BreakdownReportExportCsvToUri(context, it)
            }
        }
    if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {

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
                        modifier = Modifier
                            .size(170.dp)
                            .padding(10.dp)
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

            Button(onClick = {
                launcher.launch("Breakdown_report.csv")
                // Trigger CSV generation and download
            }) {
                Text("Download Breakdown Report")
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
                    DownTimeCard(totalDowntime)
                }

                // Breakdown Reason Card
                item {
                    BreakdownReasonCard(
                        totalDowntime = totalDowntime.toString(),
                        mttr = mttr.toString(),
                        mtbf = mtbf.toString(),
                        prediction = predictionText
                    )
                }

                // MTBF Card
                item {
                    MTBFCard(mtbf)
                }

                // Prediction Card
                item {
                    PredictionCard(predictionText)
                }

                // MTTR Card
                item {
                    MTTRCard(mttr)
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
    } else {
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
                        modifier = Modifier
                            .size(170.dp)
                            .padding(10.dp)
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

            Button(onClick = {
                launcher.launch("Breakdown_report.csv")
                // Trigger CSV generation and download
            }) {
                Text("Download Breakdown Report")
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
                    DownTimeCard(totalDowntime)
                }

                // Breakdown Reason Card
                item {
                    BreakdownReasonCard(
                        totalDowntime = totalDowntime.toString(),
                        mttr = mttr.toString(),
                        mtbf = mtbf.toString(),
                        prediction = predictionText
                    )
                }

                // MTBF Card
                item {
                    MTBFCard(mtbf)
                }

                // Prediction Card
                item {
                    PredictionCard(predictionText)
                }

                // MTTR Card
                item {
                    MTTRCard(mttr)
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
fun DownTimeCard(totalDowntime: Int) {
    val totalShiftTime = 480 // in minutes
    val percentage = if (totalShiftTime > 0) {
        (totalDowntime.toFloat() / totalShiftTime) * 100
    } else 0f
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
                            modifier = Modifier
                                .size(50.dp)
                                .padding(10.dp)
                        )
                        Text(
                            text = "Percentage of total time: ${"%.1f".format(percentage)}%",
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
                            modifier = Modifier
                                .size(50.dp)
                                .padding(10.dp)
                        )
                        Text(
                            text = "Actual minutes: $totalDowntime min",
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
fun BreakdownReasonCard(
    totalDowntime: String,
    mttr: String,
    mtbf: String,
    prediction: String
) {
    val viewModel :BreakDownViewModel=viewModel()
    val saveSuccess  by viewModel.saveSuccess



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
    if(saveSuccess){
        AlertDialog(
            onDismissRequest = {viewModel.resetSuccessFlag()},
            confirmButton = {
                TextButton(
                    onClick = {viewModel.resetSuccessFlag()}
                ) {
                    Text("OK")
                }
            },
            title = { Text("Success") },
            text = { Text("Data saved successfully.") }
        )
    }

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
                        val selectedReasons =
                            reasons.filterIndexed { index, _ -> checkedStates[index] }
                        viewModel.saveSelectedReason(selectedReasons,
                            downtime = totalDowntime,
                            mttr = mttr,
                            mtbf = mtbf,
                            prediction = prediction)
                        Log.d("BreakdownReasonCard", "Selected Reasons: $selectedReasons")
                        checkedStates.forEachIndexed { index, _ -> checkedStates[index] = false }
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
fun MTBFCard(mtbf: Float) {
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
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp)
                    )
                    Text(
                        text = "Mean time to breakdown failure:$mtbf min",
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
fun PredictionCard(predictionText: String) {
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
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp)
                    )
                    Text(
                        text = "Next Breakdown Expected In:$predictionText days",
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
fun MTTRCard(mttr: Float) {
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
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp)
                    )
                    Text(
                        text = "Mean Time to Repair:$mttr days",
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

fun BreakdownReportExportCsvToUri(context: Context, uri: Uri) {
    try {
        CoroutineScope(Dispatchers.IO).launch {
            val csvContext = getBreakDownDataAsCsv(context)
            withContext(Dispatchers.Main) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvContext.toByteArray())
                }
                println("csv file exported successfully ")
            }
        }
    } catch (e: Exception) {
        println("Csv Export failed  ${e.message}")
        e.printStackTrace()
    }
}

suspend fun getBreakDownDataAsCsv(context: Context): String {
    val db = DatabaseClass.getDatabase(context)
    val allData = db.breakDownReasonDao().getAll()

    val CsvHeader = "Id,Reasons,timestamp"
    val CsvRows = allData.joinToString(separator = "\n") { data ->
        "${data.id},${data.reasons},=\"${data.timestamp}\""
    }
    return "$CsvHeader\n$CsvRows"
}
