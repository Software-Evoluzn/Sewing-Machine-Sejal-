package com.example.jetpackcomposeevoluznsewingmachine.TableClass

import androidx.room.Entity
import androidx.room.PrimaryKey


//create new table  for storing the last reset maintenance time
@Entity(tableName = "maintenance_log")
 data class MaintenanceLog(
    @PrimaryKey(autoGenerate = true)
     val id:Int=0,
     val maintenance_time:String
 )
