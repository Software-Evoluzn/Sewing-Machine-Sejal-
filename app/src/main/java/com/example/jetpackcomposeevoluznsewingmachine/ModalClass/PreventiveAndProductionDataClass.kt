package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

import androidx.compose.ui.graphics.painter.Painter

data class PreventiveAndProductionDataClass(
    val title:String,
    val icon:Painter,
    val arrowIcon: Painter,
    val arrowIconClick: () -> Unit

    )
