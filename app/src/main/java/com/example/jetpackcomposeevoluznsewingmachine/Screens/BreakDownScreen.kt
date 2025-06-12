package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

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

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv"))
        { uri ->
            uri?.let {
                BreakdownReportExportCsvToUri(context, it)
            }
        }

    if (isPortrait) {
        // Portrait layout
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
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.aquarelle_logo),
                        contentDescription = "logo",
                        modifier = Modifier.size(50.dp).align (Alignment.TopStart)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "BREAKDOWN IN GARMENT INDUSTRY",
                    fontSize = 14.sp,
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
                item { DownTimeCard(isPortrait = true,totalDowntime) }
                item { MTBFCard(isPortrait = true,mtbf) }
                item { PredictionCard(isPortrait = true,predictionText) }
                item { MTTRCard(isPortrait = true,mttr) }
                item { BreakdownReasonCard(isPortrait = true,
                    totalDowntime = totalDowntime.toString(),
                    mttr = mttr.toString(),
                    mtbf = mtbf.toString(),
                    prediction = predictionText) }
            }

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Powered by ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Thin,
                    fontFamily = dmRegular,
                    color = Color(0xFF424242)
                )
                Text(
                    text = "EVOLUZN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF424242)
                )
            }
        }
    } else {
        // Landscape layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with Logo and Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Optional padding
                contentAlignment = Alignment.Center
            ) {
                // Centered Title Text
                Text(
                    text = "BREAKDOWN IN GARMENT INDUSTRY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )

                // Logo on the left
                Image(
                    painter = painterResource(R.drawable.aquarelle_logo),
                    contentDescription = "logo",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(60.dp)
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
                item { DownTimeCard(isPortrait = false,totalDowntime) }
                item { MTBFCard(isPortrait = false,mtbf) }
                item { PredictionCard(isPortrait = false,predictionText) }
                item { MTTRCard(isPortrait = false,mttr) }
                item { BreakdownReasonCard(isPortrait = false,
                    totalDowntime = totalDowntime.toString(),
                    mttr = mttr.toString(),
                    mtbf = mtbf.toString(),
                    prediction = predictionText) }
            }

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Powered by ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Thin,
                    fontFamily = dmRegular,
                    color = Color(0xFF424242)
                )
                Text(
                    text = "EVOLUZN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF424242)
                )
            }
        }
    }
}


@Composable
fun DownTimeCard(
    isPortrait: Boolean = true,
    totalDowntime: Int
) {
    val totalShiftTime = 480 // in minutes
    val percentage = if (totalShiftTime > 0) {
        (totalDowntime.toFloat() / totalShiftTime) * 100
    } else 0f
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "downTimeCardScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .height(if (isPortrait) 260.dp else 280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPortrait) 16.dp else 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Down Time – % and Min",
                fontSize = if (isPortrait) 16.sp else 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = dmRegular,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (isPortrait) 24.dp else 28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.percent_break_down),
                    contentDescription = "percent icon",
                    modifier = Modifier.size(if (isPortrait) 25.dp else 30.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Down Time Percent: ${percentage.toInt()} %",
                    fontSize = if (isPortrait) 16.sp else 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = dmRegular,
                    color = Color(0xFFEE0A41),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(if (isPortrait) 24.dp else 28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.timer_break_down),
                    contentDescription = "stopwatch icon",
                    modifier = Modifier.size(if (isPortrait) 30.dp else 35.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Down Time Minutes: $totalDowntime min",
                    fontSize = if (isPortrait) 16.sp else 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = dmRegular,
                    color = Color(0xFF18A21A),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun BreakdownReasonCard(isPortrait: Boolean = true,
                            totalDowntime: String,
                            mttr: String,
                            mtbf: String,
                            prediction: String) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val viewModel : BreakDownViewModel =viewModel()
    val saveSuccess  by viewModel.saveSuccess

    val reasons = listOf(
        "Belt Problem",
        "Components Issue",
        "Machine Head & Feeder Problem",
        "Coil and Water Not Receiving Issue",
        "Electrical & Mechanical Issue",
        "Not Lifting and Oil Leaking",
        "Sensor and Mechanism Adjust Issue",
        "Electrical & Mechanical Issue",
        "Not Lifting and Oil Leaking",
        "Sensor and Mechanism Adjust Issue",
        "Electrical & Mechanical Issue",
        "Not Lifting and Oil Leaking",
        "Sensor and Mechanism Adjust Issue",
        "Electrical & Mechanical Issue",
        "Not Lifting and Oil Leaking",
        "Sensor and Mechanism Adjust Issue"
    )

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
    val checkedStates = remember { mutableStateListOf(*Array(reasons.size) { false }) }
    var startAnimation by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "cardAnimation"
    )

    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .height(if (isPortrait) 320.dp else 280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPortrait) 14.dp else 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Breakdown Reason",
                fontSize = if (isPortrait) 16.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = dmRegular,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Checkbox scrollable list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
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
                            ),
                            modifier = Modifier.size(if (isPortrait) 20.dp else 20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reason,
                            fontSize = if (isPortrait) 12.sp else 12.sp,
                            fontFamily = dmRegular,
                            color = Color(0xFF333333),
                            modifier = Modifier.weight(1f),
                            lineHeight = if (isPortrait) 16.sp else 14.sp
                        )
                    }
                }
            }

            // Footer Row: Feedback & Submit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Feedback",
                    fontSize = if (isPortrait) 14.sp else 14.sp,
                    fontFamily = dmRegular,
                    color = Color(0xFF4299E1),
                    modifier = Modifier
                        .clickable { showFeedbackDialog = true }
                        .padding(8.dp)
                )

                Box(
                    modifier = Modifier
                        .background(Color(0xFF4299E1), RoundedCornerShape(6.dp))
                        .clickable {
                            val selectedReasons =
                                reasons.filterIndexed { index, _ -> checkedStates[index] }
                            viewModel.saveSelectedReason(selectedReasons,
                                downtime = totalDowntime,
                                mttr = mttr,
                                mtbf = mtbf,
                                prediction = prediction,
                                feedback = feedbackText)
                            Log.d("BreakdownReasonCard", "Selected Reasons: $selectedReasons")
                            checkedStates.forEachIndexed { index, _ -> checkedStates[index] = false }

                        }
                        .padding(vertical = 8.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Submit",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = dmRegular
                    )
                }
            }
        }
    }

    // Feedback Dialog
    if (showFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    Log.d("Feedback", "Submitted feedback: $feedbackText")
                    showFeedbackDialog = false
                }) {
                    Text("Submit", color = Color(0xFF4299E1))
                }
            },
            dismissButton = {
                TextButton(onClick = { showFeedbackDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Feedback") },
            text = {
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Enter your feedback") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
fun MTBFCard(
    isPortrait: Boolean = true,
    mtbf: Float
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    // Animation trigger state
    var startAnimation by remember { mutableStateOf(false) }

    // Smooth scale animation from 0.8f to 1f
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "mtbfCardScale"
    )

    // Trigger animation when this composable enters composition
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .height(if (isPortrait) 260.dp else 280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPortrait) 16.dp else 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "MTBF",
                fontSize = if (isPortrait) 16.sp else 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = dmRegular,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.timer_break_down),
                    contentDescription = "clock",
                    modifier = Modifier.size(if (isPortrait) 40.dp else 40.dp)
                )

                Text(
                    text = "Mean Time Between Failures",
                    fontSize = if (isPortrait) 14.sp else 14.sp,
                    fontFamily = dmRegular,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "$mtbf min",
                    fontSize = if (isPortrait) 20.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF1FA225),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun PredictionCard(
    isPortrait: Boolean = true,
    predictionDays: String
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    // Animation trigger state
    var startAnimation by remember { mutableStateOf(false) }

    // Smooth scale animation from 0.8f to 1f
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "predictionCardScale"
    )

    // Trigger animation when this composable enters composition
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .height(if (isPortrait) 260.dp else 280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPortrait) 16.dp else 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Prediction Of Breakdown",
                fontSize = if (isPortrait) 14.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = dmRegular,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.image_break_down),
                    contentDescription = "prediction",
                    modifier = Modifier.size(if (isPortrait) 40.dp else 50.dp)
                )

                Text(
                    text = "Next Breakdown Expected In",
                    fontSize = if (isPortrait) 14.sp else 14.sp,
                    fontFamily = dmRegular,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "$predictionDays days",
                    fontSize = if (isPortrait) 20.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFFE59218),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun MTTRCard(
    isPortrait: Boolean = true,
    mttrDays: Float
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    // Animation trigger state
    var startAnimation by remember { mutableStateOf(false) }

    // Smooth scale animation from 0.8f to 1f
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "mttrCardScale"
    )

    // Trigger animation when this composable enters composition
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .height(if (isPortrait) 260.dp else 280.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPortrait) 16.dp else 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "MTTR",
                fontSize = if (isPortrait) 16.sp else 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = dmRegular,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.timer_break_down),
                    contentDescription = "clock",
                    modifier = Modifier.size(if (isPortrait) 40.dp else 50.dp)
                )

                Text(
                    text = "Mean Time To Repair",
                    fontSize = if (isPortrait) 14.sp else 14.sp,
                    fontFamily = dmRegular,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "$mttrDays days",
                    fontSize = if (isPortrait) 20.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmRegular,
                    color = Color(0xFF5E9612),
                    textAlign = TextAlign.Center
                )
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

    val CsvHeader = "Id,Reasons,timestamp,DownTime,MTBF,MTTR,Prediction,Feedback"
    val CsvRows = allData.joinToString(separator = "\n") { data ->
        "${data.id},${data.reasons},=\"${data.timestamp}\",${data.downtime},${data.mtbf},${data.mttr},${data.prediction},${data.feedback}"
    }
    return "$CsvHeader\n$CsvRows"
}
