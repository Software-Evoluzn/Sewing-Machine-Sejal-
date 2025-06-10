package com.example.jetpackcomposeevoluznsewingmachine

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MissingDataLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WatchDogService: Service() {
    private var isMissing = false
    private var missingStart:Long = 0L
    private var watchdogJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startWatchdog()
        return START_STICKY
    }

    private fun startWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(10_000)
                val lastTime = getLastTimeReceivedData()
                val now = System.currentTimeMillis()
                val delta = now - lastTime

                if (!isMissing && delta > 10_000) {
                    isMissing = true
                    missingStart = lastTime + 10_000
                }

                if (isMissing && delta <= 10_000) {
                    isMissing = false
                    val end = now
                    logMissingInterval(missingStart, end)
                }
            }
        }
    }

    private fun getLastTimeReceivedData(): Long {
        val sharedPref = getSharedPreferences("machine_prefs", MODE_PRIVATE)
        return sharedPref.getLong("last_data_time", System.currentTimeMillis())
    }


    private suspend fun logMissingInterval(startMillis: Long, endMillis: Long) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startTs = sdf.format(Date(startMillis))
        val endTs = sdf.format(Date(endMillis))

        val dao = DatabaseClass.getDatabase(applicationContext).missingDataLogDao()
        dao.insert(MissingDataLog(start = startTs, end = endTs))
        println("Logged missing data interval: $startTs → $endTs")
    }


    override fun onDestroy() {
        watchdogJob?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}