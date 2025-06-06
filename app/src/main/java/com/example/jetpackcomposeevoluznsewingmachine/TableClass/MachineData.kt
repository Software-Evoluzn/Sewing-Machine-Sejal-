package com.example.jetpackcomposeevoluznsewingmachine.TableClass

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "machine_data")
data class MachineData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: String,
    val runtime: Int,
    val idleTime: Int,
    val temperature: Double,
    val vibration: Double,
    val oilLevel: Int,
    val pushBackCount:Int,
    val stitchCount:Int,
    val bobbinThread:Int
)



