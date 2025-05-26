package com.example.jetpackcomposeevoluznsewingmachine

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.CombineGraphHourDataShowing
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DailySummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourlyData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineDataLive
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.OneHourCombineGraphData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.RealTimeRunTimeData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.SetRangeCombineGraph
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.WeeklyData
import kotlinx.coroutines.flow.Flow


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
        SUM(idleTime) AS totalIdleTime,
        SUM(stitchCount) AS totalStitchCount,
        SUM(pushBackCount) As totalPushBackCount,
        ROUND(SUM(bobbinThread*2.54),2) AS totalBobbinThread,
        
        CASE 
            WHEN SUM(bobbinThread) > 0 THEN 
                ROUND(CAST(SUM(stitchCount) AS FLOAT) / SUM(bobbinThread), 2)
            ELSE 0
        END AS stitchPerBobbin
        
        
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
          AVG(vibration) AS avg_vibration,      
        AVG(oilLevel) AS avg_oilLevel          
    FROM machine_data
    WHERE strftime('%W', dateTime) = strftime('%W', 'now') 
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
          AVG(vibration) AS avg_vibration,      
        AVG(oilLevel) AS avg_oilLevel          
    FROM machine_data
    WHERE date(dateTime) BETWEEN date(:startDate) AND date(:endDate)
    GROUP BY date(dateTime)
    ORDER BY date(dateTime)
""")
    fun getDailySummary(startDate: String, endDate: String): LiveData<List<DailySummary>>


    //combine runtme ,idletime,production graph queries

    //for showing particular hour  and individual cycle  time

    @Query("""
              SELECT
                    dateTime,
                    runtime,
                    idleTime,
                    (runtime + idleTime) AS total_time_per_cycle
                     FROM machine_data
                     WHERE pushBackCount = 1
                     AND strftime('%H', dateTime) = :selectedHour
                     AND date(dateTime) = date('now')
                     ORDER BY dateTime;
            """)
    fun getIndividualHourData(selectedHour:String):Flow<List<OneHourCombineGraphData>>


    //showing today hourly data of showing cycles
    @Query("""
    WITH hours AS (
        SELECT '00' AS hour UNION ALL SELECT '01' UNION ALL SELECT '02' UNION ALL
        SELECT '03' UNION ALL SELECT '04' UNION ALL SELECT '05' UNION ALL
        SELECT '06' UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL
        SELECT '09' UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL
        SELECT '12' UNION ALL SELECT '13' UNION ALL SELECT '14' UNION ALL
        SELECT '15' UNION ALL SELECT '16' UNION ALL SELECT '17' UNION ALL
        SELECT '18' UNION ALL SELECT '19' UNION ALL SELECT '20' UNION ALL
        SELECT '21' UNION ALL SELECT '22' UNION ALL SELECT '23'
    )
    SELECT 
        h.hour AS hour,
        CAST(COUNT(m.id) AS INTEGER) AS cycle_count,
        CAST(IFNULL(SUM(m.runtime), 0) AS INTEGER) AS total_runtime,
        CAST(IFNULL(SUM(m.idleTime), 0) AS INTEGER) AS total_idleTime
    FROM hours h
    LEFT JOIN machine_data m
        ON strftime('%H', m.dateTime) = h.hour
        AND date(m.dateTime) = date('now')
        AND m.pushBackCount = 1
    GROUP BY h.hour
    ORDER BY h.hour
""")
    fun getCalculateTotalNumberOfCycleToday(): Flow<List<CombineGraphHourDataShowing>>




    //showing the selected date range data

    @Query("""
       WITH RECURSIVE date_range(day) AS (
    SELECT date(:startDate)
    UNION ALL
    SELECT date(day, '+1 day')
    FROM date_range
    WHERE day < date(:endDate)
)
SELECT day,
       COUNT(m.id) AS cycle_count,
       IFNULL(SUM(m.runtime), 0) AS total_runtime,
       IFNULL(SUM(m.idleTime), 0) AS total_idleTime
FROM date_range
LEFT JOIN machine_data m
  ON substr(m.dateTime, 1, 10) = day
  AND m.pushBackCount = 1
GROUP BY day
ORDER BY day;

    """)
    fun getNumberOfCyclerBySelectedDateRange(startDate:String,endDate:String):Flow<List<SetRangeCombineGraph>>


    @Query("""
        WITH hours AS (
    SELECT '00' AS hour UNION ALL SELECT '01' UNION ALL SELECT '02' UNION ALL
    SELECT '03' UNION ALL SELECT '04' UNION ALL SELECT '05' UNION ALL
    SELECT '06' UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL
    SELECT '09' UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL
    SELECT '12' UNION ALL SELECT '13' UNION ALL SELECT '14' UNION ALL
    SELECT '15' UNION ALL SELECT '16' UNION ALL SELECT '17' UNION ALL
    SELECT '18' UNION ALL SELECT '19' UNION ALL SELECT '20' UNION ALL
    SELECT '21' UNION ALL SELECT '22' UNION ALL SELECT '23'
)
SELECT h.hour,
       COUNT(m.id) AS cycle_count,
       IFNULL(SUM(m.runtime), 0) AS total_runtime,
       IFNULL(SUM(m.idleTime), 0) AS total_idleTime
FROM hours h
LEFT JOIN machine_data m
  ON strftime('%H', m.dateTime) = h.hour
  AND date(m.dateTime) = :selectedDate
  AND m.pushBackCount = 1
GROUP BY h.hour
ORDER BY h.hour;
    """)
    fun getNumberOfCyclesBySelectedSameDate(selectedDate:String):Flow<List<CombineGraphHourDataShowing>>





   // showing individual cycle timing and number of cycles by selected same date
    @Query("""
        SELECT
  dateTime,
  runtime,
  idleTime,
  (runtime + idleTime) AS total_time_per_cycle
FROM machine_data
WHERE pushBackCount = 1
  AND strftime('%H', dateTime) = :selectHour
  AND date(dateTime) = date(:selectedDate)
ORDER BY dateTime;
    """)
    fun getShowingIndividualCycleShowingInSameDate(selectedDate:String,selectHour:String):Flow<List<OneHourCombineGraphData>>



    //get showing weekly data of combined graph

    @Query("""
             WITH days AS (
             SELECT '0' AS day_num UNION ALL SELECT '1' UNION ALL SELECT '2' UNION ALL
             SELECT '3' UNION ALL SELECT '4' UNION ALL SELECT '5' UNION ALL SELECT '6'
             )
            SELECT 
                  CASE d.day_num
                  WHEN '0' THEN 'Sunday'
                  WHEN '1' THEN 'Monday'
                  WHEN '2' THEN 'Tuesday'
                  WHEN '3' THEN 'Wednesday'
                  WHEN '4' THEN 'Thursday'
                  WHEN '5' THEN 'Friday'
                  WHEN '6' THEN 'Saturday'
                  END AS day,
                  CAST(COUNT(m.id) AS INTEGER) AS cycle_count,
                  CAST(IFNULL(SUM(m.runtime), 0) AS INTEGER) AS total_runtime,
                  CAST(IFNULL(SUM(m.idleTime), 0) AS INTEGER) AS total_idleTime
                  FROM days d
                  LEFT JOIN machine_data m
                  ON strftime('%w', m.dateTime) = d.day_num
                  AND date(m.dateTime) BETWEEN date('now', 'weekday 0', '-6 days') AND date('now', 'weekday 0')
                  AND m.pushBackCount = 1
                  GROUP BY d.day_num
                  ORDER BY d.day_num
              """)
    fun getWeeklyDataCombinedGraph(): Flow<List<SetRangeCombineGraph>>


}