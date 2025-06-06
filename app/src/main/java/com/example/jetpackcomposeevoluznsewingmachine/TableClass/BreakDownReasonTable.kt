package com.example.jetpackcomposeevoluznsewingmachine.TableClass

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "BreakDown_Reasons")
data class BreakDownReasonTable(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val reasons:String,
    val timestamp: String = getCurrentDateTime()
    


)

fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
