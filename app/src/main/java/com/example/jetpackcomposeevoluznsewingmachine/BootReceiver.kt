package com.example.jetpackcomposeevoluznsewingmachine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startService(Intent(context, UsbSerialService::class.java))
            context.startForegroundService(Intent(context,RuntimeMonitorService::class.java))
            context.startService(Intent(context, WatchDogService::class.java))
        }
    }
}
