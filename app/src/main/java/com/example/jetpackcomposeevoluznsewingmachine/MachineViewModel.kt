package com.example.jetpackcomposeevoluznsewingmachine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.DailySummary
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.HourlyData
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.WeeklyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MachineViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseClass.getDatabase(application).machineDataDao()
      //live data
      val latestRunTime: LiveData<Int?> = dao.getLatestRunTime()
    val todayIdleTime: LiveData<Int?> = dao.getLatestIdleTime()
    val latestTempValue:LiveData<Double?> = dao.getLatestTemperatureData()
    val latestVibValue:LiveData<Double?> = dao.getLatestVibrationData()
    val latestOilLevelValue:LiveData<Int?> = dao.getLatestOilLevelData()


//    //hourly data
//    val todayHourlyRuntime: LiveData<List<HourlyData>> = dao.getHourlyDataToday()
//
//    //weekly data
//    val weeklyData: LiveData<List<WeeklyData>> = dao.getWeeklyData()
//
//    //selected date range  data
//    private val _selectedDateRangeData = MutableLiveData<List<DailySummary>>()
//    val selectedDateRangeData: LiveData<List<DailySummary>> = _selectedDateRangeData
//
//    // Function to fetch daily summary for a given date range
//    fun fetchDailySummary(startDate: String, endDate: String) {
//        dao.getDailySummary(startDate, endDate).observeForever { summaries ->
//            _selectedDateRangeData.postValue(summaries)
//        }
//    }




    val temperatureTrend = MutableLiveData<List<Double>>() // Change type to List<Double>

    fun fetch7DayTemperatureTrend() {
        viewModelScope.launch(Dispatchers.IO) {
            val rawTrend = dao.get7DayTemperatureTrend()

            println("📊 Raw trend from DB: $rawTrend")

            // Fill missing days with 0.0
            val fullTrend = (0..6).map { dayOfWeek ->
                rawTrend.find { it.dayOfWeek == dayOfWeek }?.averageTemperature ?: 0.0
            }

            println("✅ Full 7-day trend: $fullTrend")

            temperatureTrend.postValue(fullTrend)
        }
    }







}





