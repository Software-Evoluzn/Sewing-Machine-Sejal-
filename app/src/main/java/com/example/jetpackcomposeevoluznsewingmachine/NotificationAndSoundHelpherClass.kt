package com.example.jetpackcomposeevoluznsewingmachine

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.SoundPool
import android.os.Build
import androidx.annotation.RequiresApi


class NotificationAndSoundHelpherClass {

    private var soundPool: SoundPool? = null
    private var buzzerSoundId: Int = 0
    private var isBuzzerLoaded = false

    private var onLoadCompleteCallback: (() -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun NotificationFunction(context: Context ,title: String, message: String) {
        val channel_id = "OIL_LEVEL_ALERT"
        val channelName = "Oil Level Alerts"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channel_id,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies when critical values are reached"
        }
        notificationManager.createNotificationChannel(channel)


        val builder = Notification.Builder(context, channel_id)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(Notification.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(999, builder.build())


    }

    fun initBuzzerSound(context: Context, onLoadComplete: (() -> Unit)? = null) {
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()

        buzzerSoundId = soundPool!!.load(context, R.raw.buzzer_song, 1)
        onLoadCompleteCallback = onLoadComplete

        soundPool!!.setOnLoadCompleteListener { _, sampleId, _ ->
            if (sampleId == buzzerSoundId) {
                isBuzzerLoaded = true
                println("buzzer sound loaded")
                onLoadComplete?.invoke()
            }
        }
    }


    fun PlayBuzzerSound() {
        if (isBuzzerLoaded) {
            soundPool?.play(buzzerSoundId, 1f, 1f, 0, 0, 1f)
            println("BUZZER Playing buzzer sound")
        } else {
            println("BUZZER Buzzer sound not loaded yet")
        }
    }

    fun releaseSoundPool() {
        soundPool?.release()
        soundPool = null
    }
}
