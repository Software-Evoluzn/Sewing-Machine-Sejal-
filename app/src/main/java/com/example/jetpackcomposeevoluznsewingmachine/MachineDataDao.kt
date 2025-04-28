package com.example.jetpackcomposeevoluznsewingmachine

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DailySummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourlyData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineDataLive
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.WeeklyData


@Dao
 interface MachineDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(data: MachineData)

        //latest data starts

    @Query("""
    SELECT 
        (SELECT temperature FROM machine_data WHERE date(dateTime) = date('now') ORDER BY id DESC LIMIT 1) AS latestTemperature,
        (SELECT vibration FROM machine_data WHERE date(dateTime) = date('now') ORDER BY id DESC LIMIT 1) AS latestVibration,
        (SELECT oilLevel FROM machine_data WHERE date(dateTime) = date('now') ORDER BY id DESC LIMIT 1) AS latestOilLevel,
        SUM(runtime) AS totalRuntime,
        SUM(idleTime) AS totalIdleTime
    FROM machine_data
    WHERE date(dateTime) = date('now')
""")
    fun getLatestMachineData(): LiveData<MachineDataLive>

    //latest data ends


    //hourly data showing



    @Query("""
    WITH hours AS (
        SELECT '00' AS hour
        UNION ALL SELECT '01'
        UNION ALL SELECT '02'
        UNION ALL SELECT '03'
        UNION ALL SELECT '04'
        UNION ALL SELECT '05'
        UNION ALL SELECT '06'
        UNION ALL SELECT '07'
        UNION ALL SELECT '08'
        UNION ALL SELECT '09'
        UNION ALL SELECT '10'
        UNION ALL SELECT '11'
        UNION ALL SELECT '12'
        UNION ALL SELECT '13'
        UNION ALL SELECT '14'
        UNION ALL SELECT '15'
        UNION ALL SELECT '16'
        UNION ALL SELECT '17'
        UNION ALL SELECT '18'
        UNION ALL SELECT '19'
        UNION ALL SELECT '20'
        UNION ALL SELECT '21'
        UNION ALL SELECT '22'
        UNION ALL SELECT '23'
    )
    SELECT 
        hours.hour,
        IFNULL(SUM(CASE WHEN date(machine_data.dateTime) = date('now') THEN machine_data.runtime ELSE 0 END), 0) AS total_runtime,
        IFNULL(SUM(CASE WHEN date(machine_data.dateTime) = date('now') THEN machine_data.idleTime ELSE 0 END), 0) AS total_idle_time,
        AVG(CASE WHEN date(machine_data.dateTime) = date('now') THEN machine_data.temperature ELSE NULL END) AS avg_temperature,
        AVG(CASE WHEN date(machine_data.dateTime) = date('now') THEN machine_data.vibration ELSE NULL END) AS avg_vibration,
        AVG(CASE WHEN date(machine_data.dateTime) = date('now') THEN machine_data.oilLevel ELSE NULL END) AS avg_oilLevel
    FROM hours
    LEFT JOIN machine_data
        ON hours.hour = strftime('%H', machine_data.dateTime)
    GROUP BY hours.hour
    ORDER BY hours.hour ASC
""")
    fun getHourlyDataToday(): LiveData<List<HourlyData>>




//    //showing weekly data starts


    @Query("""
    SELECT
        CASE strftime('%w', dateTime)
            WHEN '0' THEN 'Sunday'
            WHEN '1' THEN 'Monday'
            WHEN '2' THEN 'Tuesday'
            WHEN '3' THEN 'Wednesday'
            WHEN '4' THEN 'Thursday'
            WHEN '5' THEN 'Friday'
            WHEN '6' THEN 'Saturday'
        END AS day_of_week,
        SUM(runtime) AS total_runtime,
        SUM(idleTime) AS total_idle_time,
        AVG(temperature) AS avg_temperature,
          AVG(vibration) AS avg_vibration,      -- <-- ADD THIS
        AVG(oilLevel) AS avg_oilLevel          -- <-- ADD THIS
    FROM machine_data
    WHERE strftime('%W', dateTime) = strftime('%W', 'now')  -- current week
    GROUP BY strftime('%w', dateTime)
    ORDER BY strftime('%w', dateTime) ASC
""")
    fun getWeeklyData(): LiveData<List<WeeklyData>>



//    //showing selected date range

    @Query("""
    SELECT
        date(dateTime) AS date,
        SUM(runtime) AS total_runtime,
        SUM(idleTime) AS total_idle_time,
        AVG(temperature) AS avg_temperature,
          AVG(vibration) AS avg_vibration,      -- <-- ADD THIS
        AVG(oilLevel) AS avg_oilLevel          -- <-- ADD THIS
    FROM machine_data
    WHERE date(dateTime) BETWEEN date(:startDate) AND date(:endDate)
    GROUP BY date(dateTime)
    ORDER BY date(dateTime)
""")
    fun getDailySummary(startDate: String, endDate: String): LiveData<List<DailySummary>>












}