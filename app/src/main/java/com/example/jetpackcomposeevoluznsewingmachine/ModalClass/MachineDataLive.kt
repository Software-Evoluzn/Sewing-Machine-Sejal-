package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

data class MachineDataLive(
    val latestTemperature: Double?,
    val latestVibration: Double?,
    val latestOilLevel: Int?,
    val totalRuntime: Int?,
    val totalIdleTime: Int?,
    val totalStitchCount:Int?,
    val totalBobbinThread:Float,
    val stitchPerBobbin :Float
)
