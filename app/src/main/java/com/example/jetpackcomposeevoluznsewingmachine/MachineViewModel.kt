package com.example.jetpackcomposeevoluznsewingmachine


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.CombineGraphHourDataShowing
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DailySummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourlyData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineDataLive
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.RealTimeRunTimeData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.WeeklyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class MachineViewModel(application: Application) : AndroidViewModel(application)
 {
     private val dao = DatabaseClass.getDatabase(application).machineDataDao()


//     //showing individual hour data
//    fun getCombineGraphHourData(selectedHour:String) {
//        return dao.getIndividualHourData(selectedHour)
//    }

     //showing individual day combined graph
     var getCombineGraphOfTodayData: Flow<List<CombineGraphHourDataShowing>> = dao.getCalculateTotalNumberOfCycleToday()

//     var CombinedGraphRunTime : Flow<List<Int>> =getCombineGraphOfTodayData.map {list->
//         list.map { it.total_runtime }
//     }
//
//     var CombinedGraphIdleTime : Flow<List<Int>> =getCombineGraphOfTodayData.map{list->
//         list.map{it.total_idletime}
//
//     }
//     var cycleCountOfCompbineGraphOfToday :Flow<List<Int>> = getCombineGraphOfTodayData.map{list->
//         list.map{it.cycle_count}
//
//     }
//     var hourCountInCombineGraphOfToday :Flow<List<String>> = getCombineGraphOfTodayData.map{list->
//         list.map{it.hour}
//
//     }





//     //showing selectedDateRange combine graph
//     fun getCombinedGraphOfSelectedDateRange(startDate:String,endDate:String){
//         return dao.getNumberOfCyclerBySelectedDateRange(startDate,endDate)
//     }

//     //showing selected same date combine graph data
//     fun getCombinedGraphOfSameDate(selectedDate:String){
//         return dao.getNumberOfCyclesBySelectedSameDate(selectedDate)
//     }
//
//     //showing selected same date individual hour data
//     fun getCombinedGraphIndividualCyclesOfSameDate(selectedDate:String,selectedHour:String){
//         return dao.getShowingIndividualCycleShowingInSameDate(selectedDate,selectedHour)
//     }






     val realTimeData: Flow<List<RealTimeRunTimeData>> = dao.getRecentRuntimeData()

     val realTimeRunTimeData: Flow<List<Int>> = realTimeData.map { list ->
         list.map { (it.runtime) }
     }
     val realTimeSecond: Flow<List<String>> = realTimeData.map { list ->
         list.map { it.dateTime }

     }
     val realTimePushBackCount: Flow<List<Int>> = realTimeData.map { list ->
         list.map { (it.pushBackCount) }

     }

     val realIdleTime: Flow<List<Int>> = realTimeData.map { list ->
         list.map { (it.idleTime) }
     }

     //live data
      val latestMachineData: LiveData<MachineDataLive> = dao.getLatestMachineData()

    // Transforming the runtime and idle time into hours
    val latestRunTime: LiveData<Float> = latestMachineData.map { result ->
        val runtimeInSeconds = result.totalRuntime ?: 0
        runtimeInSeconds / 3600f // Convert to hours
    }



    val latestIdleTime: LiveData<Float> = latestMachineData.map { result ->
        val idleTimeInSeconds = result.totalIdleTime ?: 0
        idleTimeInSeconds / 3600f // Convert to hours
    }

     val latestStitchCount:LiveData<Int> = latestMachineData.map{ result ->
         val totalStitchCount =result.totalStitchCount?:0
         totalStitchCount

     }

     val latestPushBackCount:LiveData<Int> = latestMachineData.map{result ->
         val totalPushBackCount=result.totalPushBackCount?:0
         totalPushBackCount

     }

     val latestBobbinThread:LiveData<Float> = latestMachineData.map{result->
         val totalBobbinThread=result.totalBobbinThread?:0
         totalBobbinThread.toFloat()

     }
     val latestSPI:LiveData<Int> = latestMachineData.map{result ->
         val totalSPI=result.stitchPerBobbin
         totalSPI.toInt()

     }


    // Mapping the other values directly from the result
    val latestTempValue: LiveData<Double?> = latestMachineData.map { it.latestTemperature }
    val latestVibValue: LiveData<Double?> = latestMachineData.map { it.latestVibration }
    val latestOilLevelValue: LiveData<Int?> = latestMachineData.map { it.latestOilLevel }


    //hourly data
    // hourly data
    private val todayHourlyData: LiveData<List<HourlyData>> = dao.getHourlyDataToday()

    // temperature list
    val todayTemperatureList: LiveData<List<Double>> = todayHourlyData.map { list ->
        val hourToTemp = list.associateBy { it.hour.toIntOrNull() ?: -1 }
//        Log.d("TodayTempList", "Hourly Temps: $list")
        List(24) { hour ->
            hourToTemp[hour]?.avg_temperature ?: 0.0
        }
    }

    // vibration list
    val todayVibrationList: LiveData<List<Double>> = todayHourlyData.map { list ->
      val hourToVib = list.associateBy {  it.hour.toIntOrNull() ?: -1 }
        List(24){ hour ->
            hourToVib[hour]?.avg_vibration ?: 0.0

        }

    }

    // oil level list
    val todayOilLevelList: LiveData<List<Double>> = todayHourlyData.map { list ->
       val hourToOilLevel=list.associateBy { it.hour.toIntOrNull() ?: -1 }
        List(24){ hour ->
            hourToOilLevel[hour]?.avg_oilLevel?.toDouble() ?: 0.0
        }

    }

    // Convert runtime from seconds to hours
    val todayRuntimeList: LiveData<List<Double>> = todayHourlyData.map { list ->
        val hourToRunTime = list.associateBy { it.hour.toIntOrNull() ?: -1 }
        List(24){ hour ->
            (hourToRunTime[hour]?.total_runtime?.toDouble() ?: 0.0)/3600

        }
    }

    // Convert idle time from seconds to hours
    val todayIdleTimeList: LiveData<List<Double>> = todayHourlyData.map { list ->
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
    private val _selectedDateRangeData = MutableLiveData<List<DailySummary>>()
    val selectedDateRangeData: LiveData<List<DailySummary>> = _selectedDateRangeData
    // Function to fetch daily summary for a given date range
    fun fetchDailySummary(startDate: String, endDate: String) {
        dao.getDailySummary(startDate, endDate).observeForever { summaries ->
            _selectedDateRangeData.postValue(summaries)
        }
    }

}





