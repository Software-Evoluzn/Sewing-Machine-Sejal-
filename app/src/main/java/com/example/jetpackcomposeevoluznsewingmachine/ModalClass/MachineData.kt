package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

@Entity(tableName = "machine_data")
data class MachineData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: String? = currentTime,
    val runtime: Int,
    val idleTime: Int,
    val temperature: Double,
    val vibration: Double,
    val oilLevel: Int
)


