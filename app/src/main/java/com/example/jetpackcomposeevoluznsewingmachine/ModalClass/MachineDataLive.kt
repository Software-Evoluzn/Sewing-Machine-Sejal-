package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

data class MachineDataLive(
    val latestTemperature: Double?,
    val latestVibration: Double?,
    val latestOilLevel: Double?,
    val total_stitch_count:Int?,
    val total_pushback_count:Int,
    val total_bobbin_thread:Float,
    val total_rpm_count:Int,
    val stitchPerInch :Float,
    val activeRunTimeSec: Int?,
)
