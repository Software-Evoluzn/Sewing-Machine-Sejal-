package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

data class ProductionCartItemList(
    val title:String,
    val value:String,
    val unit:String,
    val icon: Painter,
    val arrowIcon: Painter,
    val onClick:() -> Unit,
    val valueColor: Color
)
