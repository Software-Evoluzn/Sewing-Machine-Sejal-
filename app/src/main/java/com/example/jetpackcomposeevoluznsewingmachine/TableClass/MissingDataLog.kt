package com.example.jetpackcomposeevoluznsewingmachine.TableClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missing_data_log")
data class MissingDataLog(
          @PrimaryKey(autoGenerate = true) val id:Int=0,
          val timeStamp:String

)
