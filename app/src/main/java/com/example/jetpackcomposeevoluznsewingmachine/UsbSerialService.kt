package com.example.jetpackcomposeevoluznsewingmachine

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.IBinder
import android.util.Log
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineData
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class UsbSerialService : Service() {

    private var usbSerialPort: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null
    private var running = false
    private val dataBuffer = StringBuilder()


    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && device != null) {
                        startSerialConnection()
                    }
                }

                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    startSerialConnection()
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    stopSerialConnection()
                    val intent=Intent("USB_DEVICE_DEATTACHED")
                    sendBroadcast(intent)
                    stopSelf()  // Optionally stop service when device detaches
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerUsbReceiver()
        startSerialConnection()  // Try connecting at service start
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun registerUsbReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        registerReceiver(usbReceiver, filter)
    }

    private fun startSerialConnection() {
        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)

        if (availableDrivers.isEmpty()) return

        val driver = availableDrivers[0]

        if (!usbManager.hasPermission(driver.device)) {
            val permissionIntent = PendingIntent.getBroadcast(
                this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(driver.device, permissionIntent)
            return
        }

        connection = usbManager.openDevice(driver.device)
        usbSerialPort = driver.ports[0]

        try {
            usbSerialPort?.apply {
                open(connection)
                setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                running = true
                startReading()
            }
        } catch (e: Exception) {
            Log.e("UsbSerialService", "Connection Error: ${e.message}")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startReading() {
        Thread {
            val buffer = ByteArray(64)
            while (running) {
                try {
                    val bytesRead = usbSerialPort?.read(buffer, 1000) ?: 0
                    if (bytesRead > 0) {
                        val chunk = buffer.copyOf(bytesRead).decodeToString()
                        dataBuffer.append(chunk)  // Keep adding new chunks

                        // Process complete messages (assuming your device sends \n or \r\n at the end)
                        var index: Int
                        while (true) {
                            index = dataBuffer.indexOf("\n")  // Or "\r\n" if that's what your device uses
                            if (index == -1) break  // no full message yet

                            val completeMessage = dataBuffer.substring(0, index).trim()
                            dataBuffer.delete(0, index + 1)  // remove processed part

                            println(completeMessage)

                            val parts = completeMessage.split(":")
                            if (parts.size == 6) {
                                val runtime = parts[0].toIntOrNull() ?: 0
                                val temp = parts[1].toDoubleOrNull() ?: 0.0
                                val vibration = parts[2].toDoubleOrNull() ?: 0.0
                                val oilLevel = parts[3].toIntOrNull() ?: 0
                                val pushBackCount=parts[4].toIntOrNull()?:0
                                val stichCount=parts[5].toIntOrNull()?:0
                                val idleTime = if(runtime==1) 0 else 1

                                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                                    Date()
                                )

                                val data = MachineData(
                                    dateTime = currentTime,
                                    runtime = runtime,
                                    idleTime = idleTime,
                                    temperature = temp,
                                    vibration = vibration,
                                    oilLevel = oilLevel,
                                    pushBackCount = pushBackCount,
                                    stitchCount = stichCount
                                )

                                GlobalScope.launch(Dispatchers.IO) {
                                       try{
                                                DatabaseClass.getDatabase(applicationContext).machineDataDao().insert(data)
                                                println("data inserted Successfully")
                                       }catch (e:Exception){
                                           println("DB insert error ${e.message}")
                                       }

                                }
                            }


//                            handleFullMessage(completeMessage)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("UsbSerialService", "Read Error: ${e.message}")
                    running = false
                }
            }
        }.start()
    }



    private fun stopSerialConnection() {
        running = false
        try {
            usbSerialPort?.close()
            connection?.close()
        } catch (e: Exception) {
            Log.e("UsbSerialService", "Disconnection Error: ${e.message}")
        }
        usbSerialPort = null
        connection = null
    }

    override fun onDestroy() {
        unregisterReceiver(usbReceiver)
        stopSerialConnection()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_USB_PERMISSION = "com.example.USB_PERMISSION"
    }

}
