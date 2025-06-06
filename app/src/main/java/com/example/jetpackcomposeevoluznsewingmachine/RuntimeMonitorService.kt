package com.example.jetpackcomposeevoluznsewingmachine

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.jetpackcomposeevoluznsewingmachine.DaoClass.MachineDataDao
import com.example.jetpackcomposeevoluznsewingmachine.DaoClass.MaintenanceLogDao
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MaintenanceLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RuntimeMonitorService : Service() {

    private lateinit var database:DatabaseClass
    private lateinit var machineDataDao:MachineDataDao
    private lateinit var maintenanceDao:MaintenanceLogDao
    private var preNotificationShown = false

    private val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private  var monitorJob : Job?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        database = DatabaseClass.getDatabase(this)
        machineDataDao = database.machineDataDao()
        maintenanceDao = database.maintenanceLogDao()
       println("on create of runtime monitoring servicing class")
        startForegroundNotification()
        startMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("on start command")
        return START_STICKY
    }

    private fun startForegroundNotification() {
        val channelId = "runtime_monitor_channel"
        val channelName = "Runtime Monitor"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // Use HIGH not LOW
            ).apply {
                description = "Shows runtime monitoring in background"
                setShowBadge(true)
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            println("here is  notification channel ")

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Maintenance Monitoring")
            .setContentText("Machine runtime is being monitored.")
            .setSmallIcon(R.drawable.preventive_maintainance)
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // Use Compat
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(1, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMonitoring() {
        monitorJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val lastMaintenance = maintenanceDao.getMaintenanceLog()
                val lastTimeStr = lastMaintenance?.maintenance_time ?: "1970-01-01 00:00:00"
                println("lastTime maintenace of monitor job ")
                val runTimeSec = machineDataDao.getRunTimeSince(lastTimeStr) ?: 0
                val runTimeHrs = runTimeSec/3600f

                if (runTimeHrs in 48f..50f && !preNotificationShown) {
                    triggerPreNotification()
                    preNotificationShown = true
                }

                if (runTimeHrs >=50f) {
                    val currentTimeStr = sdf.format(Date(System.currentTimeMillis()))
                    maintenanceDao.insertMaintenanceLog(MaintenanceLog(maintenance_time = currentTimeStr))
                    preNotificationShown = false // reset flag for next cycle
                }

                delay(60_000) // check every minute
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun triggerPreNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pre_notification_channel"

        val channel = NotificationChannel(
            channelId,
            "Pre-Maintenance Alert",
            NotificationManager.IMPORTANCE_HIGH // must be HIGH for heads-up
        ).apply {
            description = "Alerts user when 2 hours left for maintenance"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Maintenance Due Soon")
            .setContentText("Only 2 hours left before maintenance is due!")
            .setSmallIcon(R.drawable.ic_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Use COMPAT
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)

        // Vibrate for emphasis
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))

        Log.d(TAG, "Pre-notification shown with vibration")
    }






    override fun onBind(intent: Intent?): IBinder? =null

    override fun onDestroy() {
        monitorJob?.cancel()
        super.onDestroy()
    }
}