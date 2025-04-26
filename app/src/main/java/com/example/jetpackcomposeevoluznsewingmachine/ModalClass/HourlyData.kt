package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

data class HourlyData(
    val hour: String,
    val total_runtime: Int,
    val total_idle_time: Int,
    val avg_temperature: Double,
    val avg_vibration:Double,
    val avg_oilLevel:Int
)

