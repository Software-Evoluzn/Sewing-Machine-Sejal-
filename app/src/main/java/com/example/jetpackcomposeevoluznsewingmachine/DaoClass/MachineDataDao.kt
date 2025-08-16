package com.example.jetpackcomposeevoluznsewingmachine.DaoClass

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.CombineGraphHourDataShowing
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DailySummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourSummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourlyData
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MachineData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineDataLive
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.OneHourCombineGraphData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.SetRangeCombineGraph
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.WeeklyData
import kotlinx.coroutines.flow.Flow


@Dao
interface MachineDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: MachineData)


    @Query("select * from machine_data")
    fun getMachineDataConvertToCSVFile(): List<MachineData>


    //latest data starts

    @Query(
        """
          WITH 
             todays_data AS (
             SELECT *
             FROM machine_data
             WHERE date(dateTime) = date('now')
            ),
          ordered_data AS (
               SELECT *
               FROM todays_data
               ORDER BY dateTime DESC
           ),
          latest_row AS (
               SELECT *
               FROM ordered_data
               LIMIT 1
           ),
          last_3_rows AS (
               SELECT temperature, vibration
               FROM ordered_data
               LIMIT 3
          ),
          avg_temp_last_3 AS (
               SELECT AVG(temperature) AS avg_temp
               FROM last_3_rows
          ),
          avg_vib_last_3 AS (
               SELECT AVG(vibration) AS avg_vib
               FROM last_3_rows
          ),
          prev_valid_temp AS (
               SELECT temperature
               FROM ordered_data
               WHERE temperature > 0
               ORDER BY dateTime DESC
               LIMIT 1
          ),
          prev_valid_vibration AS (
               SELECT vibration
               FROM ordered_data
               WHERE vibration >= 0
               ORDER BY dateTime DESC
               LIMIT 1
          ),
          stitch_progress_data AS (
          SELECT 
          t.*,
                 (SELECT stitchCount FROM todays_data td
                 WHERE td.dateTime < t.dateTime
                 ORDER BY td.dateTime DESC LIMIT 1) AS prev_stitch_count,
                 (SELECT bobbinThread FROM todays_data td
                 WHERE td.dateTime < t.dateTime
                 ORDER BY td.dateTime DESC LIMIT 1) AS prev_bobbin,
                 (SELECT dateTime FROM todays_data td
                 WHERE td.dateTime < t.dateTime
                 ORDER BY td.dateTime DESC LIMIT 1) AS prev_time
                 FROM todays_data t
              ),
              active_runtime AS (
                  SELECT COUNT(DISTINCT strftime('%s', dateTime)) AS production_time_in_sec
                  FROM todays_data
                  WHERE stitchCount > 0
                  AND bobbinThread > 0
              ),
              total_stitch AS (
                  SELECT SUM(stitchCount) AS total_stitch_count
                  FROM todays_data
                  WHERE bobbinThread > 0
                  AND stitchCount > 0
               ),
              total_bobbin AS (
                   SELECT ROUND(SUM(bobbinThread) * (5.1 / 8), 2) AS total_bobbin_thread
                   FROM todays_data
                   WHERE bobbinThread > 0
                   AND stitchCount > 0
              ),
              latest_rpm AS (
                   SELECT rpmCount AS total_rpm_count
                   FROM ordered_data
                   LIMIT 1
               ),
              pushback_count AS (
                   SELECT COUNT(*) AS total_pushback_count
                   FROM (
                   SELECT 
                   dateTime,
                   pushBackCount,
                   stitchCount,
                   bobbinThread,
                   prev_stitch_count AS prev_stitch,
                   prev_bobbin,
                   CAST(STRFTIME('%s', dateTime) AS INTEGER) - CAST(STRFTIME('%s', prev_time) AS INTEGER) AS time_diff_sec
                   FROM stitch_progress_data
              ) AS sub
                WHERE pushBackCount > 0
                AND stitchCount = prev_stitch
                AND bobbinThread = prev_bobbin
                AND time_diff_sec BETWEEN 0 AND 2
              ),
              last_minute_agg AS (
                  SELECT 
                  SUM(stitchCount) AS sum_stitch,
                  SUM(bobbinThread) AS sum_bobbin
                  FROM machine_data
                  WHERE dateTime >= datetime('now', "-60 seconds")
                  AND bobbinThread > 0
                  AND stitchCount > 0
               )
             SELECT
              CASE
                    WHEN lr.temperature <= 0 THEN pvt.temperature
                    WHEN ABS(lr.temperature - atl3.avg_temp) > (atl3.avg_temp * 0.2) THEN pvt.temperature
                    WHEN ABS(lr.temperature - atl3.avg_temp) > 25 THEN atl3.avg_temp
                    WHEN lr.temperature >= 45 THEN atl3.avg_temp
                    ELSE lr.temperature
                    END AS latestTemperature,
              CASE
                    WHEN lr.vibration <= 0 THEN pvv.vibration
                    WHEN ABS(lr.vibration - avl3.avg_vib) > (avl3.avg_vib * 0.2) THEN pvv.vibration
                    ELSE lr.vibration
                    END AS latestVibration,
                    lr.oilLevel AS latestOilLevel,
                    ts.total_stitch_count,
                    pb.total_pushback_count,
                    tb.total_bobbin_thread,
                    lrpm.total_rpm_count,
              CASE
                    WHEN lma.sum_bobbin > 0 THEN ROUND(
                    CAST(lma.sum_stitch AS FLOAT) /
                    ((lma.sum_bobbin * (5.1 / 8)) / 2.54),2)
              ELSE 0
              END AS stitchPerInch,
              ar.production_time_in_sec AS activeRuntimeSec
              FROM latest_row lr
              JOIN avg_temp_last_3 atl3
              JOIN avg_vib_last_3 avl3
              JOIN prev_valid_temp pvt
              JOIN prev_valid_vibration pvv
              JOIN total_stitch ts
              JOIN total_bobbin tb
              JOIN latest_rpm lrpm
              JOIN pushback_count pb
              JOIN last_minute_agg lma
              JOIN active_runtime ar;

              """
    )
    fun getLatestMachineData(): LiveData<MachineDataLive>

    //latest data ends


    //hourly data showing
    @Query(
        """
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
"""
    )
    fun getHourlyDataToday(): Flow<List<HourlyData>>


    //showing weekly data starts
    @Query(
        """
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
"""
    )
    fun getWeeklyData(): LiveData<List<WeeklyData>>


//    //showing selected date range
    @Query(
        """
            WITH RECURSIVE date_range(day) AS (
            SELECT date(:startDate)
            UNION ALL
            SELECT date(day, '+1 day')
            FROM date_range
            WHERE day < date(:endDate)
          )
         SELECT 
             day,
            IFNULL(AVG(m.temperature), 0) AS avg_temperature,
            IFNULL(AVG(m.vibration), 0) AS avg_vibration,
            IFNULL(AVG(m.oilLevel), 0) AS avg_oilLevel
            FROM date_range
            LEFT JOIN machine_data m
            ON substr(m.dateTime, 1, 10) = day
            GROUP BY day
            ORDER BY day;
           """
    )
    fun getDailySummary(startDate: String, endDate: String): Flow<List<DailySummary>>


    //user select same date for that showing 23 hour data
    @Query(
        """
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
    h.hour,
    AVG(m.temperature) AS avg_temperature,
    AVG(m.vibration) AS avg_vibration,
    AVG(m.oilLevel) AS avg_oilLevel
FROM hours h
LEFT JOIN machine_data m
  ON strftime('%H', m.dateTime) = h.hour
  AND date(m.dateTime) = date(:date)
GROUP BY h.hour
ORDER BY h.hour
"""
    )
    fun getHourlySummaryForDate(date: String): Flow<List<HourSummary>>


    //combine runtme ,idletime,production graph queries

    //for showing particular hour  and individual cycle  time

//    @Query(
//        """
//              SELECT
//                    dateTime,
//                    runtime,
//                    idleTime,
//                    (runtime + idleTime) AS total_time_per_cycle
//                     FROM machine_data
//                     WHERE pushBackCount = 1
//                     AND strftime('%H', dateTime) = :selectedHour
//                     AND date(dateTime) = date('now')
//                     ORDER BY dateTime;
//            """
//    )
//    fun getIndividualHourData(selectedHour: String): Flow<List<OneHourCombineGraphData>>


    //showing today hourly data of showing cycles
    @Query(
        """
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
"""
    )
    fun getCalculateTotalNumberOfCycleToday(): Flow<List<CombineGraphHourDataShowing>>


    //showing the selected date range data

    @Query(
        """
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

    """
    )
    fun getNumberOfCyclerBySelectedDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<SetRangeCombineGraph>>


    @Query(
        """
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
    """
    )
    fun getNumberOfCyclesBySelectedSameDate(selectedDate: String): Flow<List<CombineGraphHourDataShowing>>


//    // showing individual cycle timing and number of cycles by selected same date
//    @Query(
//        """
//        SELECT
//  dateTime,
//  runtime,
//  idleTime,
//  (runtime + idleTime) AS total_time_per_cycle
//FROM machine_data
//WHERE pushBackCount = 1
//  AND strftime('%H', dateTime) = :selectHour
//  AND date(dateTime) = date(:selectedDate)
//ORDER BY dateTime;
//    """
//    )
//    fun getShowingIndividualCycleShowingInSameDate(
//        selectedDate: String,
//        selectHour: String
//    ): Flow<List<OneHourCombineGraphData>>


    //get showing weekly data of combined graph

    @Query(
        """
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
              """
    )
    fun getWeeklyDataCombinedGraph(): Flow<List<SetRangeCombineGraph>>


    @Query("SELECT SUM(runtime) FROM machine_data WHERE datetime(dateTime) > datetime(:lastMaintenanceTime)")
    suspend fun getRunTimeSince(lastMaintenanceTime: String): Int?


}