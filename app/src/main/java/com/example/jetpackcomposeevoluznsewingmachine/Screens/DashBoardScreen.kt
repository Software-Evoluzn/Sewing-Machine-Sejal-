package com.example.jetpackcomposeevoluznsewingmachine.Screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseBackupHelper
import com.example.jetpackcomposeevoluznsewingmachine.MachineViewModel
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.BlurCardData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.CardItemList
import com.example.jetpackcomposeevoluznsewingmachine.R
import java.io.File


@Composable
fun DashBoardLiveScreen(navController: NavController) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))


        var showCardByBackgroundBlur by remember { mutableStateOf(false) }
        var selectedCardData by remember{mutableStateOf<BlurCardData?>(null)}
        val context = LocalContext.current
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
                uri?.let {
                    backUpDataAndExport(context, it)
                }

            }

    Box(modifier=Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Heading with text and image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.aquarelle_logo),
                    contentDescription = "logo",
                    modifier = Modifier.size(70.dp).align(Alignment.TopStart)
                )
                Text(
                    text = "SEWING MACHINE LIVE DASHBOARD",
                    fontSize = 24.sp,
                    fontFamily = dmRegular,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B4B4B),
                    modifier = Modifier.align(Alignment.Center)
                )
                Image(
                    painter = painterResource(R.drawable.download_img),
                    contentDescription = "Download",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(67.dp)
                        .clickable {
                            launcher.launch("machine_database_backup.db")
                        }
                        .padding(end = 30.dp)
                )
            }

            // Cards grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Main dashboard grid - takes most space
                Box(
                    modifier = Modifier
                        .weight(10f)
                        .fillMaxHeight()
                ) {
                    DashBoardScreen(navController = navController,
                        onShowBlurCardRequested = {
                            selectedCardData =it
                            showCardByBackgroundBlur = true })
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Oil level indicator - smaller width
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    OilLevelIndicator()
                }
            }

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
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

    if (showCardByBackgroundBlur && selectedCardData != null) {
        val liveValue by selectedCardData!!.value.observeAsState()
        val formattedValue=liveValue?.toString()?:"0"
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)) // dim background
                .clickable { showCardByBackgroundBlur = false }, // tap outside to dismiss
            contentAlignment = Alignment.Center
        ) {
            val dmRegular = FontFamily(Font(R.font.dmsans_regular))

            // Animation trigger state
            var startAnimation by remember { mutableStateOf(false) }

            // Smooth scale animation from 0.8f to 1f
            val scale by animateFloatAsState(
                targetValue = if (startAnimation) 1f else 0.8f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                label = "parameterBoxScale"
            )

            // Trigger animation when this composable enters composition
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
                    // ❌ Close icon at top-right
                    Image(
                        painter = painterResource(R.drawable.close_icon),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clickable { showCardByBackgroundBlur = false }
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
                            text = selectedCardData!!.title,
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
                                text = formattedValue, // e.g., "23.6"
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = dmRegular,
                                color = selectedCardData!!.valueColor
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = selectedCardData!!.unit, // e.g., "mm/s" or "°C"
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



}


@Composable
fun DashBoardScreen(navController: NavController,
                    onShowBlurCardRequested: (BlurCardData) -> Unit) {

    val viewModel:MachineViewModel= viewModel()


    val cardItemList=listOf(
        CardItemList("PRODUCTION",
            onCardClick = {navController.navigate("machineRuntimeScreen")},
            painterResource(R.drawable.production_icon)),
        CardItemList("MAINTENANCE",
            onCardClick = {navController.navigate("maintenanceScreen")},
            painterResource(R.drawable.maintenance_icon)),
        CardItemList("QUALITY",
            onCardClick = {},
            painterResource(R.drawable.quality_icon)),
        CardItemList("STITCH COUNT",
            onCardClick = {
                onShowBlurCardRequested(
                    BlurCardData(
                        title = "STITCH COUNT",
                        value =viewModel.latestStitchCount,
                        unit ="count",
                        valueColor =Color(0xFF9C27B0)

                    )
                )
            },
            painterResource(R.drawable.spi_icon)),
        CardItemList("BREAKDOWN",
            onCardClick = {},
            painterResource(R.drawable.break_down_icon)),
        CardItemList("TRAINING",
            onCardClick = {},
            painterResource(R.drawable.training_icon)),
        CardItemList("BOBBIN THREAD",
            onCardClick = {
                onShowBlurCardRequested(
                    BlurCardData(
                        title = "BOBBIN THREAD",
                        value =viewModel.latestBobbinThread,
                        unit ="count",
                        valueColor =Color(0xFFF44336)

                    )
                )
            },
            painterResource(R.drawable.bobbin_icon)),
        CardItemList("SPI",
            onCardClick = {
                onShowBlurCardRequested(
                    BlurCardData(
                        title = "SPI",
                        value =viewModel.latestSPI,
                        unit ="stitch/inch",
                        valueColor =Color(0xFF4CAF50)

                    )
                )
            },
            painterResource(R.drawable.stitch_count_icon)),
        )

    LazyVerticalGrid(
        columns=GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)


    ) {
        items(cardItemList) { card ->
            ShowingCard(
                title = card.title,
                onCardClick = card.onCardClick,
                icon =card.icon
                )
        }

    }
}

@Composable
fun ShowingCard(
    title: String,
    modifier:Modifier=Modifier,
    onCardClick: ()->Unit,
    icon: Painter
) {
    val dmRegular = FontFamily(Font(R.font.dmsans_regular))

    // Animation: initial state is 0.8f, target is 1.0f
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "scaleAnimation"
    )
    // Trigger the animation once when the Composable enters
    LaunchedEffect(Unit) {
        startAnimation = true
    }
    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {  onCardClick() }
            .padding(8.dp)
            .defaultMinSize(minWidth = 100.dp)
            .height(120.dp)
            .border(width = 0.5.dp,Color(0xFFD0D0D3), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6F6)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {



            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = icon,
                    contentDescription = "Section Icon",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontFamily = dmRegular,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Oil Level")
@Composable
fun OilLevelIndicator() {
    val viewModel: MachineViewModel = viewModel()
    val oilLevelValue by viewModel.latestOilLevelValue.observeAsState()

    // Convert oil level from 0-100 to 0.0 - 1.0
    val oilLevel = ((oilLevelValue ?: 0).coerceIn(0, 100)) / 100f

    val containerHeight = 300.dp
    val containerWidth = 60.dp
    val tubeWidth = 30.dp
    val circleSize = 50.dp

    Box(
        modifier = Modifier
            .width(containerWidth)
            .height(containerHeight)
    ) {
        // Thermometer background
        Box(
            modifier = Modifier
                .width(tubeWidth)
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(50.dp))
        )

        // Oil fill
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

        // Circle at the top of the fill
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












fun backUpDataAndExport(context: Context, uri: Uri) {
    try{

       DatabaseBackupHelper.backupDatabase(context)
        val backUpFile= File(context.getExternalFilesDir(null),"backup_machine_database.db")
        if(!backUpFile.exists()){
            println("backupFile does not exists")
               return

        }


        //write the file to the selected Uri

        context.contentResolver.openOutputStream(uri)?.use{outputStream ->
            backUpFile.inputStream().use{inputStream->
                inputStream.copyTo(outputStream)

            }

        }
        Toast.makeText(context,"Database file exported successfully",Toast.LENGTH_SHORT).show()
        println("database file export to external directly successfully")


    }
    catch(e:Exception){
        Toast.makeText(context,"Export failed: ${e.message}",Toast.LENGTH_SHORT).show()
        println("database file not download due to some error!")
        e.printStackTrace()

    }


}

