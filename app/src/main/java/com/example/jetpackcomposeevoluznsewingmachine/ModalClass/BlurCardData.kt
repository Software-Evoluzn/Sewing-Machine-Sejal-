package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData

data class BlurCardData(
    val title:String,
    val value: LiveData<out Number >,
    val unit:String,

    val valueColor: Color,
)
