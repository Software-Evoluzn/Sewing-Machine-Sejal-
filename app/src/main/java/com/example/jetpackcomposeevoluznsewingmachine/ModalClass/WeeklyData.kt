package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

data class WeeklyData(
    val day_of_week: String,
    val total_runtime: Int,
    val total_idle_time: Int,
    val avg_temperature: Double,
    val avg_vibration:Double,
    val avg_oilLevel:Int
)

