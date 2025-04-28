package com.example.jetpackcomposeevoluznsewingmachine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DailySummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourlyData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineDataLive
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.WeeklyData



class MachineViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseClass.getDatabase(application).machineDataDao()
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

    // Mapping the other values directly from the result
    val latestTempValue: LiveData<Double?> = latestMachineData.map { it.latestTemperature }
    val latestVibValue: LiveData<Double?> = latestMachineData.map { it.latestVibration }
    val latestOilLevelValue: LiveData<Int?> = latestMachineData.map { it.latestOilLevel }


    //hourly data
    // hourly data
    private val todayHourlyData: LiveData<List<HourlyData>> = dao.getHourlyDataToday()

    // temperature list
    val todayTemperatureList: LiveData<List<Double>> = todayHourlyData.map { list ->
        MutableList(24) { 0.0 }.apply {
            list.forEach { data ->
                val hour = data.hour.toIntOrNull() ?: 0
                if (hour in 0..23) {
                    this[hour] = data.avg_temperature.toDouble() ?: 0.0
                }
            }
        }
    }

    // vibration list
    val todayVibrationList: LiveData<List<Double>> = todayHourlyData.map { list ->
        MutableList(24) { 0.0 }.apply {
            list.forEach { data ->
                val hour = data.hour.toIntOrNull() ?: 0
                if (hour in 0..23) {
                    this[hour] = data.avg_vibration.toDouble() ?: 0.0
                }
            }
        }
    }

    // oil level list
    val todayOilLevelList: LiveData<List<Double>> = todayHourlyData.map { list ->
        MutableList(24) { 0.0 }.apply {
            list.forEach { data ->
                val hour = data.hour.toIntOrNull() ?: 0
                if (hour in 0..23) {
                    this[hour] = data.avg_oilLevel.toDouble() ?: 0.0
                }
            }
        }
    }

    // Convert runtime from seconds to hours
    val todayRuntimeList: LiveData<List<Double>> = todayHourlyData.map { list ->
        MutableList(24) { 0.0 }.apply {
            list.forEach { data ->
                val hour = data.hour.toIntOrNull() ?: 0
                if (hour in 0..23) {
                    this[hour] = (data.total_runtime.toDouble() / 3600) // Convert seconds to hours
                }
            }
        }
    }

    // Convert idle time from seconds to hours
    val todayIdleTimeList: LiveData<List<Double>> = todayHourlyData.map { list ->
        MutableList(24) { 0.0 }.apply {
            list.forEach { data ->
                val hour = data.hour.toIntOrNull() ?: 0
                if (hour in 0..23) {
                    this[hour] = (data.total_idle_time.toDouble() / 3600) // Convert seconds to hours
                }
            }
        }
    }


    //weekly data
    val weeklyData: LiveData<List<WeeklyData>> = dao.getWeeklyData()


    //weekly temperature
    val weeklyTemperatureList: LiveData<List<Double>> = weeklyData.map { list ->
        val temperatures = MutableList(7) { 0.0 } // 7 days
        list.forEachIndexed { index, data ->
            temperatures[index] = data.avg_temperature
        }
        temperatures
    }

    //weekly vibration
    val weeklyVibrationList: LiveData<List<Double>> = weeklyData.map { list ->
        val vibration = MutableList(7) { 0.0 } // 7 days
        list.forEachIndexed { index, data ->
            vibration[index] = data.avg_vibration
        }
        vibration
    }

    //weekly oilLevel
    val weeklyOilLevelList: LiveData<List<Double>> = weeklyData.map { list ->
        val oilLevel = MutableList(7) { 0.0 } // 7 days
        list.forEachIndexed { index, data ->
            oilLevel[index] = data.avg_oilLevel.toDouble()
        }
        oilLevel
    }

    //weekly runtime
    val weeklyRunTimeList: LiveData<List<Double>> = weeklyData.map { list ->
        val runtime = MutableList(7) { 0.0 } // 7 days
        list.forEachIndexed { index, data ->
            runtime[index] = (data.total_runtime.toDouble()/3600)
        }
        runtime
    }

    //weekly idle time
    val weeklyIdleTimeList: LiveData<List<Double>> = weeklyData.map { list ->
        val idleTime = MutableList(7) { 0.0 } // 7 days
        list.forEachIndexed { index, data ->
            idleTime[index] = (data.total_idle_time.toDouble()/3600)
        }
        idleTime
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





