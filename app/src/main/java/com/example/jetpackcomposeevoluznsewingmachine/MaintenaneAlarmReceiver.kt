package com.example.jetpackcomposeevoluznsewingmachine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class MaintenaneAlarmReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val helper=NotificationAndSoundHelpherClass()

        if (context != null) {
            helper.initBuzzerSound(context){
                helper.PlayBuzzerSound()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context != null) {
                helper.NotificationFunction(context)
            }
        }

    }
}