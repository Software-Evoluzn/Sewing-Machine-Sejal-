package com.example.jetpackcomposeevoluznsewingmachine

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DailySummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DayTemperature
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourlyData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.WeeklyData


@Dao
public interface MachineDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(data: MachineData)

        //latest data starts

    @Query("SELECT temperature FROM machine_data WHERE date(dateTime) = date('now') ORDER BY id DESC LIMIT 1")
    fun getLatestTemperatureData(): LiveData<Double?>

    @Query("SELECT vibration FROM machine_data WHERE date(dateTime) = date('now') ORDER BY id DESC LIMIT 1")
    fun getLatestVibrationData(): LiveData<Double?>

    @Query("SELECT oilLevel FROM machine_data  WHERE date(dateTime) = date('now') ORDER BY id DESC LIMIT 1")
    fun getLatestOilLevelData(): LiveData<Int?>


    @Query("SELECT SUM(runtime) FROM machine_data WHERE date(dateTime) = date('now')")
    fun getLatestRunTime(): LiveData<Int?>

    @Query("SELECT SUM(idleTime) FROM machine_data WHERE date(dateTime) = date('now')")
    fun getLatestIdleTime():LiveData<Int?>

    //latest data ends


    //hourly data showing




//    @Query("""
//    SELECT
//        strftime('%H', dateTime) AS hour,
//        SUM(runtime) AS total_runtime,
//        SUM(idleTime) AS total_idle_time,
//        AVG(temperature) AS avg_temperature,
//        AVG(vibration) AS avg_vibration,
//        AVG(oilLevel) AS avg_oilLevel
//    FROM machine_data
//    WHERE date(dateTime) = date('now')
//    GROUP BY hour
//    ORDER BY hour ASC
//""")
//    fun getHourlyDataToday(): LiveData<List<HourlyData>>
//
//    //hourly data ends
//
//    //showing weekly data starts
//
//
//    @Query("""
//    SELECT
//        CASE strftime('%w', dateTime)
//            WHEN '0' THEN 'Sunday'
//            WHEN '1' THEN 'Monday'
//            WHEN '2' THEN 'Tuesday'
//            WHEN '3' THEN 'Wednesday'
//            WHEN '4' THEN 'Thursday'
//            WHEN '5' THEN 'Friday'
//            WHEN '6' THEN 'Saturday'
//        END AS day_of_week,
//        SUM(runtime) AS total_runtime,
//        SUM(idleTime) AS total_idle_time,
//        AVG(temperature) AS avg_temperature
//    FROM machine_data
//    WHERE strftime('%W', dateTime) = strftime('%W', 'now')  -- current week
//    GROUP BY strftime('%w', dateTime)
//    ORDER BY strftime('%w', dateTime) ASC
//""")
//    fun getWeeklyData(): LiveData<List<WeeklyData>>
//
//
//    //showing weekly data ends
//
//    //showing selected date range
//
//    @Query("""
//    SELECT
//        date(dateTime) AS date,
//        SUM(runtime) AS total_runtime,
//        SUM(idleTime) AS total_idle_time,
//        AVG(temperature) AS avg_temperature
//    FROM machine_data
//    WHERE date(dateTime) BETWEEN date(:startDate) AND date(:endDate)
//    GROUP BY date(dateTime)
//    ORDER BY date(dateTime)
//""")
//    fun getDailySummary(startDate: String, endDate: String): LiveData<List<DailySummary>>
//
//
//
//





    @Query("""
    SELECT 
        CAST (strftime('%w', dateTime) AS INTEGER) AS dayOfWeek,
        AVG(temperature) AS averageTemperature
    FROM machine_data
    WHERE dateTime >= datetime('now', '-7 days')
    GROUP BY dayOfWeek
    ORDER BY dayOfWeek ASC
""")
     fun get7DayTemperatureTrend(): List<DayTemperature>







}