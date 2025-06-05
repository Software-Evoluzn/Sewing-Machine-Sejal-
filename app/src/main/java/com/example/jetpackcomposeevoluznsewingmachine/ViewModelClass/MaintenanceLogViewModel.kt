package com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass
import com.example.jetpackcomposeevoluznsewingmachine.MaintenaneAlarmReceiver
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MaintenanceLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MaintenanceLogViewModel(application: Application):AndroidViewModel(application) {
    private val db = DatabaseClass.getDatabase(application)
    private val machineDao = db.machineDataDao()
    private val maintenanceDao = db.maintenanceLogDao()

    val runtimeState = MutableLiveData<Float>()  // in hours

    init {
        startRuntimeMonitor()
    }

    private fun startRuntimeMonitor() {
        viewModelScope.launch {
            while (true) {
                checkAndHandleRuntime()
                delay(1000) // 1-second interval
            }
        }
    }

    private suspend fun checkAndHandleRuntime() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val lastMaintenance = maintenanceDao.getMaintenanceLog()
        val lastTimeStr = lastMaintenance?.maintenance_time

        // If there's no previous maintenance time, use epoch start (or skip logic)
        val lastMaintenanceTimeStr = lastTimeStr ?: "1970-01-01 00:00:00"

        // Query runtime since last maintenance time
        val runtimeInSec = machineDao.getRunTimeSince(lastMaintenanceTimeStr) ?: 0
        val runtimeInHours = runtimeInSec.toFloat()   // Convert from seconds to hours

        runtimeState.postValue(runtimeInHours)

        when {
            runtimeInHours >= 30f -> {
                // Insert new maintenance log with current timestamp as string
                val currentTimeStr = sdf.format(Date(System.currentTimeMillis()))
                maintenanceDao.insertMaintenanceLog(MaintenanceLog(maintenance_time = currentTimeStr))
            }

            runtimeInHours >= 25f -> {
                triggerPreNotification()
            }
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun triggerPreNotification() {
       val context=getApplication<Application>().applicationContext
        val alarmIntent= Intent(context,MaintenaneAlarmReceiver::class.java).let{intent->
            PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_IMMUTABLE)
        }
        val alarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAlarmsAtMillis=System.currentTimeMillis()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAlarmsAtMillis,
            alarmIntent

        )
        println("trigger notification")
    }


}