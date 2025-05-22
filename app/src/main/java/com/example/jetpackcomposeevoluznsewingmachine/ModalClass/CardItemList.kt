package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

data class CardItemList(
    val title: String,
    val onCardClick: () -> Unit,
    val icon: Painter
)

