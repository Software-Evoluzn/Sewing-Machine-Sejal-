package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseBackupHelper
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass
import com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.BlurCardData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.CardItemList
import com.example.jetpackcomposeevoluznsewingmachine.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DashBoardLiveScreen(navController: NavController) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showCardByBackgroundBlur by remember { mutableStateOf(false) }
    var selectedCardData by remember { mutableStateOf<BlurCardData?>(null) }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv"))
        { uri ->
            uri?.let {
                exportCsvToUri(context, it)
            }
        }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLandscape) {
            // Landscape Layout (Original)
            LandscapeLayout(
                dmRegular = dmRegular,
                navController = navController,
                launcher = launcher,
                onShowBlurCardRequested = {
                    selectedCardData = it
                    showCardByBackgroundBlur = true
                }
            )
        } else {
            // Portrait Layout
            PortraitLayout(
                dmRegular = dmRegular,
                navController = navController,
                launcher = launcher,
                onShowBlurCardRequested = {
                    selectedCardData = it
                    showCardByBackgroundBlur = true
                }
            )
        }
    }

    // Blur Card Dialog (same for both orientations)
    if (showCardByBackgroundBlur && selectedCardData != null) {
        BlurCardDialog(
            selectedCardData = selectedCardData!!,
            onDismiss = { showCardByBackgroundBlur = false }
        )
    }
}

@Composable
fun LandscapeLayout(
    dmRegular: FontFamily,
    navController: NavController,
    launcher: androidx.activity.result.ActivityResultLauncher<String>,
    onShowBlurCardRequested: (BlurCardData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.aquarelle_logo),
                contentDescription = "logo",
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.TopStart)
            )
            Text(
                text = "SEWING MACHINE LIVE DASHBOARD",
                fontSize = 24.sp,
                fontFamily = dmRegular,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            Image(
                painter = painterResource(R.drawable.download_img),
                contentDescription = "Download",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(67.dp)
                    .clickable {
                        launcher.launch("machine_database_backup.csv")
                    }
                    .padding(end = 30.dp)
            )
        }

        // Main content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(10f)
                    .fillMaxHeight()
            ) {
                DashBoardScreen(
                    navController = navController,
                    onShowBlurCardRequested = onShowBlurCardRequested,
                    isPortrait = false
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                OilLevelIndicator(isPortrait = false)
            }
        }

        // Footer
        FooterSection(dmRegular = dmRegular)
    }
}

@Composable
fun PortraitLayout(
    dmRegular: FontFamily,
    navController: NavController,
    launcher: androidx.activity.result.ActivityResultLauncher<String>,
    onShowBlurCardRequested: (BlurCardData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header (Compact for Portrait)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.aquarelle_logo),
                    contentDescription = "logo",
                    modifier = Modifier.size(50.dp)
                )

                Image(
                    painter = painterResource(R.drawable.download_img),
                    contentDescription = "Download",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            launcher.launch("machine_database_backup.csv")
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SEWING MACHINE LIVE DASHBOARD",
                fontSize = 20.sp,
                fontFamily = dmRegular,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Dashboard Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                DashBoardScreen(
                    navController = navController,
                    onShowBlurCardRequested = onShowBlurCardRequested,
                    isPortrait = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Oil Level Indicator (Horizontal for Portrait)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                OilLevelIndicator(isPortrait = true)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Footer
        FooterSection(dmRegular = dmRegular, isPortrait = true)
    }
}

@Composable
fun FooterSection(dmRegular: FontFamily, isPortrait: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Powered by ",
            fontSize = if (isPortrait) 13.sp else 15.sp,
            fontWeight = FontWeight.Thin,
            fontFamily = dmRegular,
            color = Color(0xFF424242)
        )
        Text(
            text = "EVOLUZN",
            fontSize = if (isPortrait) 16.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = dmRegular,
            color = Color(0xFF424242)
        )
    }
}

@Composable
fun DashBoardScreen(
    navController: NavController,
    onShowBlurCardRequested: (BlurCardData) -> Unit,
    isPortrait: Boolean = false
) {
    val viewModel: MachineViewModel = viewModel()

    val cardItemList = listOf(
        CardItemList("PRODUCTION",
            onCardClick = { navController.navigate("machineRuntimeScreen") },
            painterResource(R.drawable.production_icon)),
        CardItemList("MAINTENANCE",
            onCardClick = { navController.navigate("maintenanceScreen") },
            painterResource(R.drawable.maintenance_icon)),
        CardItemList("QUALITY",
            onCardClick = {},
            painterResource(R.drawable.quality_icon)),
        CardItemList("STITCH COUNT",
            onCardClick = {
                onShowBlurCardRequested(
                    BlurCardData(
                        title = "STITCH COUNT",
                        value = viewModel.latestStitchCount,
                        unit = "count",
                        valueColor = Color(0xFF9C27B0)
                    )
                )
            },
            painterResource(R.drawable.spi_icon)),
        CardItemList("BREAKDOWN",
            onCardClick = { navController.navigate("breakdownScreen") },
            painterResource(R.drawable.break_down_icon)),
        CardItemList("TRAINING",
            onCardClick = {},
            painterResource(R.drawable.training_icon)),
        CardItemList("BOBBIN THREAD",
            onCardClick = {
                onShowBlurCardRequested(
                    BlurCardData(
                        title = "BOBBIN THREAD",
                        value = viewModel.latestBobbinThread,
                        unit = "count",
                        valueColor = Color(0xFFF44336)
                    )
                )
            },
            painterResource(R.drawable.bobbin_icon)),
        CardItemList("SPI",
            onCardClick = {
                onShowBlurCardRequested(
                    BlurCardData(
                        title = "SPI",
                        value = viewModel.latestSPI,
                        unit = "stitch/inch",
                        valueColor = Color(0xFF4CAF50)
                    )
                )
            },
            painterResource(R.drawable.stitch_count_icon)),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isPortrait) 2 else 4),
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isPortrait) 4.dp else 8.dp),
        contentPadding = PaddingValues(if (isPortrait) 4.dp else 8.dp),
        verticalArrangement = Arrangement.spacedBy(if (isPortrait) 6.dp else 8.dp),
        horizontalArrangement = Arrangement.spacedBy(if (isPortrait) 6.dp else 8.dp)
    ) {
        items(cardItemList) { card ->
            ShowingCard(
                title = card.title,
                onCardClick = card.onCardClick,
                icon = card.icon,
                isPortrait = isPortrait
            )
        }
    }
}

@Composable
fun ShowingCard(
    title: String,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    icon: Painter,
    isPortrait: Boolean = false
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "scaleAnimation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { onCardClick() }
            .padding(if (isPortrait) 4.dp else 8.dp)
            .defaultMinSize(minWidth = if (isPortrait) 80.dp else 100.dp)
            .height(if (isPortrait) 100.dp else 120.dp)
            .border(
                width = 0.5.dp,
                Color(0xFFD0D0D3),
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isPortrait) 12.dp else 18.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = icon,
                    contentDescription = "Section Icon",
                    modifier = Modifier.size(if (isPortrait) 32.dp else 40.dp)
                )
                Spacer(modifier = Modifier.height(if (isPortrait) 6.dp else 10.dp))
                Text(
                    text = title,
                    fontSize = if (isPortrait) 11.sp else 15.sp,
                    fontFamily = dmRegular,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun OilLevelIndicator(isPortrait: Boolean = false) {
    val viewModel: MachineViewModel = viewModel()
    val oilLevelValue by viewModel.latestOilLevelValue.observeAsState()
    val oilLevel = ((oilLevelValue ?: 0).coerceIn(0, 100)) / 100f

    if (isPortrait) {
        // Horizontal Oil Level for Portrait
        val containerWidth = 400.dp
        val containerHeight = 50.dp
        val tubeHeight = 30.dp
        val circleSize = 45.dp

        Box(
            modifier = Modifier
                .width(containerWidth)
                .height(containerHeight)
        ) {
            // Background tube
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tubeHeight)
                    .align(Alignment.Center)
                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(50.dp))
            )

            // Oil fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(oilLevel)
                    .height(tubeHeight)
                    .align(Alignment.CenterStart)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFFA500), Color.Yellow)
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
            )

            // Circle indicator
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val density = LocalDensity.current
                val widthPx = constraints.maxWidth.toFloat()
                val circlePx = with(density) { circleSize.toPx() }
                val circleOffset = widthPx * oilLevel - (circlePx / 2)
                val offsetDp = with(density) { circleOffset.toDp() }

                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .align(Alignment.CenterStart)
                        .offset(x = offsetDp.coerceIn(0.dp, containerWidth - circleSize))
                        .background(Color(0xFFFFA500), shape = CircleShape)
                        .border(2.dp, Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(oilLevel * 100).toInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    } else {
        // Vertical Oil Level for Landscape (Original)
        val containerHeight = 300.dp
        val containerWidth = 60.dp
        val tubeWidth = 30.dp
        val circleSize = 50.dp

        Box(
            modifier = Modifier
                .width(containerWidth)
                .height(containerHeight)
        ) {
            Box(
                modifier = Modifier
                    .width(tubeWidth)
                    .fillMaxHeight()
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(50.dp))
            )

            Box(
                modifier = Modifier
                    .width(tubeWidth)
                    .fillMaxHeight(oilLevel)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFA500), Color.Yellow)
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
            )

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val density = LocalDensity.current
                val heightPx = constraints.maxHeight.toFloat()
                val circlePx = with(density) { circleSize.toPx() }
                val circleOffset = heightPx * (1f - oilLevel) - (circlePx / 2)
                val offsetDp = with(density) { circleOffset.toDp() }

                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .align(Alignment.TopCenter)
                        .offset(y = offsetDp.coerceIn(0.dp, containerHeight - circleSize))
                        .background(Color(0xFFFFA500), shape = CircleShape)
                        .border(2.dp, Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(oilLevel * 100).toInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BlurCardDialog(
    selectedCardData: BlurCardData,
    onDismiss: () -> Unit
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))
    val liveValue by selectedCardData.value.observeAsState()
    val formattedValue = liveValue?.toString() ?: "0"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        var startAnimation by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0.8f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            label = "parameterBoxScale"
        )

        LaunchedEffect(Unit) {
            startAnimation = true
        }

        Card(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .padding(20.dp)
                .width(310.dp)
                .height(165.dp)
                .border(0.5.dp, Color(0xFF0C0C0C), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(R.drawable.close_icon),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clickable { onDismiss() }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedCardData.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0C0C0C),
                        fontFamily = dmRegular,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = formattedValue,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = dmRegular,
                            color = selectedCardData.valueColor
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = selectedCardData.unit,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0C0C0C),
                            fontFamily = dmRegular,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// Keep your existing functions as they are
fun backUpDataAndExport(context: Context, uri: Uri) {
    try {
        DatabaseBackupHelper.backupDatabase(context)
        val backUpFile = File(context.getExternalFilesDir(null), "backup_machine_database.db")
        if (!backUpFile.exists()) {
            println("backupFile does not exists")
            return
        }

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            backUpFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Toast.makeText(context, "Database file exported successfully", Toast.LENGTH_SHORT).show()
        println("database file export to external directly successfully")

    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        println("database file not download due to some error!")
        e.printStackTrace()
    }
}

fun exportCsvToUri(context: Context, uri: Uri) {
    try {
        CoroutineScope(Dispatchers.IO).launch {
            val csvContext = getMachineDataAsCsv(context)
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

fun getMachineDataAsCsv(context: Context): String {
    val db = DatabaseClass.getDatabase(context)
    val allData = db.machineDataDao().getMachineDataConvertToCSVFile()

    val CsvHeader = "Id,DateTime,RunTime,IdleTime,Temperature,Vibration,OilLevel,pushBackCount,StitchCount,BobbinThread"
    val CsvRows = allData.joinToString(separator = "\n") { data ->
        "${data.id},=\"${data.dateTime}\",${data.runtime},${data.idleTime},${data.temperature},${data.vibration},${data.oilLevel},${data.pushBackCount},${data.stitchCount},${data.bobbinThread}"
    }
    return "$CsvHeader\n$CsvRows"
}