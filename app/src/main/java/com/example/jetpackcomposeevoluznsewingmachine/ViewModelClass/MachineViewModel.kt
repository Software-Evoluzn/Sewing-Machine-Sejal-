package com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.CombineGraphHourDataShowing
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DailySummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourSummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourlyData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineDataLive
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.OneHourCombineGraphData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.SetRangeCombineGraph
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.WeeklyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MachineViewModel(application: Application) : AndroidViewModel(application)
 {
     private val dao = DatabaseClass.getDatabase(application).machineDataDao()


     //showing individual day combined graph
     var getCombineGraphOfTodayData: Flow<List<CombineGraphHourDataShowing>> = dao.getCalculateTotalNumberOfCycleToday()

//     fun getIndividualHourCombineGraphData(selectedHour:String):Flow<List<OneHourCombineGraphData>>{
//         return dao.getIndividualHourData(selectedHour)
//     }

     fun getSetRangeCombineGraphShow(startDate:String,endDate:String):Flow<List<SetRangeCombineGraph>>{
         return dao.getNumberOfCyclerBySelectedDateRange(startDate,endDate)
     }

     fun getSameDateCombineGraph(startDate:String):Flow<List<CombineGraphHourDataShowing>>{
         return dao.getNumberOfCyclesBySelectedSameDate(startDate)
     }

//     fun getSameDateHourDataCombineGraph(selectedDate:String,selectedHour:String):Flow<List<OneHourCombineGraphData>>{
//         return dao.getShowingIndividualCycleShowingInSameDate(selectedDate,selectedHour)
//     }

     fun getWeeklyCombinedGraph():Flow<List<SetRangeCombineGraph>>{
         return dao.getWeeklyDataCombinedGraph()
     }


     //live data
      val latestMachineData: LiveData<MachineDataLive> = dao.getLatestMachineData()

    // Transforming the runtime and idle time into hours
    val latestRunTime: LiveData<Float> = latestMachineData.map { result ->
        val runtimeInSeconds = result.activeRuntimeSec ?: 0
        runtimeInSeconds.toFloat()  // Convert to hours
    }



     val latestIdleTime: LiveData<Float> = latestMachineData.map { data ->
         if (data != null) {
             if (data.total_stitch_count == 0) {
                 // machine is idle → count idle seconds
                 data.activeRuntimeSec?.let { active ->
                     val totalTimeSec = (System.currentTimeMillis() / 1000).toInt() // since start
                     val idleSec = totalTimeSec - active
                     idleSec.toFloat()
                 } ?: 0f
             } else {
                 0f
             }
         } else {
             0f
         }
     }


     val latestStitchCount:LiveData<Int> = latestMachineData.map{ result ->
         val totalStitchCount =result.total_stitch_count?:0
         totalStitchCount

     }

     val latestPushBackCount:LiveData<Int> = latestMachineData.map{result ->
         val totalPushBackCount=result.total_pushback_count?:0
         totalPushBackCount

     }

     val latestBobbinThread:LiveData<Float> = latestMachineData.map{result->
         val totalBobbinThread=result.total_bobbin_thread?:0
         totalBobbinThread.toFloat()

     }
     val latestSPI:LiveData<Int> = latestMachineData.map{result ->
         val totalSPI=result.stitchPerInch
         totalSPI.toInt()

     }
     val latestRpmCount:LiveData<Int> =latestMachineData.map{result ->
         val totalRpmCount=result.total_rpm_count
         totalRpmCount*60

     }


    // Mapping the other values directly from the result
    val latestTempValue: LiveData<Double?> = latestMachineData.map { it.latestTemperature }
    val latestVibValue: LiveData<Double?> = latestMachineData.map { it.latestVibration }
   val latestOilLevelValue: LiveData<Double?> = latestMachineData.map { it.latestOilLevel }



    //hourly data
    // hourly data
    private val todayHourlyData: Flow<List<HourlyData>> = dao.getHourlyDataToday()

    // temperature list
    val todayTemperatureList: Flow<List<Double>> = todayHourlyData.map { list ->
        val hourToTemp = list.associateBy { it.hour.toIntOrNull() ?: -1 }
//        Log.d("TodayTempList", "Hourly Temps: $list")
        List(24) { hour ->
            hourToTemp[hour]?.avg_temperature ?: 0.0
        }
    }

    // vibration list
    val todayVibrationList: Flow<List<Double>> = todayHourlyData.map { list ->
      val hourToVib = list.associateBy {  it.hour.toIntOrNull() ?: -1 }
        List(24){ hour ->
            hourToVib[hour]?.avg_vibration ?: 0.0

        }

    }

    // oil level list
    val todayOilLevelList: Flow<List<Double>> = todayHourlyData.map { list ->
       val hourToOilLevel=list.associateBy { it.hour.toIntOrNull() ?: -1 }
        List(24){ hour ->
            hourToOilLevel[hour]?.avg_oilLevel?.toDouble() ?: 0.0
        }

    }

    // Convert runtime from seconds to hours
    val todayRuntimeList: Flow<List<Double>> = todayHourlyData.map { list ->
        val hourToRunTime = list.associateBy { it.hour.toIntOrNull() ?: -1 }
        List(24){ hour ->
            (hourToRunTime[hour]?.total_runtime?.toDouble() ?: 0.0)/3600

        }
    }

    // Convert idle time from seconds to hours
    val todayIdleTimeList: Flow<List<Double>> = todayHourlyData.map { list ->
       val hourToIdleTime = list.associateBy { it.hour.toIntOrNull() ?: -1 }
        List(24){ hour ->
            (hourToIdleTime[hour]?.total_idle_time?.toDouble() ?:0.0)/3600

        }

    }

    //weekly data
    val weeklyData: LiveData<List<WeeklyData>> = dao.getWeeklyData()
    private val dayToIndex = mapOf(
        "Sunday" to 0,
        "Monday" to 1,
        "Tuesday" to 2,
        "Wednesday" to 3,
        "Thursday" to 4,
        "Friday" to 5,
        "Saturday" to 6
    )
    val weeklyTemperatureList: LiveData<List<Double>> = weeklyData.map { list ->
        MutableList(7) { 0.0 }.apply {
            list.forEach { data ->
                val index = dayToIndex[data.day_of_week] ?: return@forEach
                this[index] = data.avg_temperature
            }
        }
    }
    //weekly vibration
    val weeklyVibrationList: LiveData<List<Double>> = weeklyData.map { list ->
        MutableList(7) { 0.0 }.apply {
            list.forEach { data ->
                val index = dayToIndex[data.day_of_week] ?: return@forEach
                this[index] = data.avg_vibration
            }
        }
    }
    //weekly oilLevel
    val weeklyOilLevelList: LiveData<List<Double>> = weeklyData.map { list ->
        MutableList(7) { 0.0 }.apply {
            list.forEach { data ->
                val index = dayToIndex[data.day_of_week] ?: return@forEach
                this[index] = data.avg_oilLevel.toDouble()
            }
        }
    }
    //weekly runtime
    val weeklyRunTimeList: LiveData<List<Double>> = weeklyData.map { list ->
        MutableList(7) { 0.0 }.apply {
            list.forEach { data ->
                val index = dayToIndex[data.day_of_week] ?: return@forEach
                this[index] = (data.total_runtime.toDouble())/3600
            }
        }
    }
    //weekly idle time
    val weeklyIdleTimeList: LiveData<List<Double>> = weeklyData.map { list ->
        MutableList(7) { 0.0 }.apply {
            list.forEach { data ->
                val index = dayToIndex[data.day_of_week] ?: return@forEach
                this[index] = (data.total_idle_time.toDouble())/3600
            }
        }
    }

    //selected date range  data


     fun getSelectedDateRangeMaintenance(startDate:String,endDate:String):Flow<List<DailySummary>>{
         return dao.getDailySummary(startDate, endDate)
     }

     fun getHourlySummaryDateOfSelectedDate(selectedDate:String):Flow<List<HourSummary>> {
         return dao.getHourlySummaryForDate(selectedDate)
     }

//     private val _dailySummary= MutableStateFlow<List<DailySummary>>(emptyList())
//     val dailySummary:StateFlow<List<DailySummary>>  = _dailySummary

//     private val _hourlySummary = MutableStateFlow<List<HourSummary>>(emptyList())
//     val hourSummary:StateFlow<List<HourSummary>> = _hourlySummary

//     fun loadDailySummary(startDate:String,endDate:String){
//         viewModelScope.launch{
//                      getSelectedDateRangeMaintenance(startDate, endDate).collect{_dailySummary.value=it}
//         }
//
//     }

//     fun loadHoulySummary(selectedDate:String){
//         viewModelScope.launch{
//                  getHourlySummaryDateOfSelectedDate(selectedDate).collect{_hourlySummary.value=it}
//         }
//     }



 }





