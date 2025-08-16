import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

import java.util.UUID

class MqttHandler(private val context: Context) {

    private val TAG = "MQTT_HANDLER"
    private val serverUri = "tcp://192.168.0.2:1889"
    private val clientId = UUID.randomUUID().toString()
    private val subscribeTopic = "/#" // subscribe to all topics
    private val publishTopic = "oeeF0BF3D/control"

    private var mqttClient: MqttAndroidClient =
        MqttAndroidClient(context, serverUri, clientId)



    /**
     * Connect to MQTT broker and subscribe
     */
    fun connectAndSubscribe() {
        val options = MqttConnectOptions().apply {
            isCleanSession = false
            keepAliveInterval = 60
            connectionTimeout = 10
//            automaticReconnect = true
            isAutomaticReconnect = true
        }

        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.w(TAG, "Connection lost: ${cause?.message}")
                Toast.makeText(context, "MQTT connection lost", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    connectAndSubscribe()
                }, 5000)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val msg = message?.toString() ?: ""
                Log.i(TAG, "Message received on topic [$topic]: $msg")
                Toast.makeText(context, "Message: $msg", Toast.LENGTH_SHORT).show()
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(TAG, "Message delivered successfully")
            }
        })

        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connected to broker")
                    subscribeToTopic(subscribeTopic)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e(TAG, "Connection failed: ${exception?.message}")
                    Toast.makeText(context, "MQTT connection failed", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: MqttException) {
            Log.e(TAG, "Exception while connecting: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Subscribe to topic
     */
    private fun subscribeToTopic(topic: String) {
        try {
            mqttClient.subscribe(topic, 1, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to topic: $topic")
                    Toast.makeText(context, "Subscribed to $topic", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e(TAG, "Subscription failed: ${exception?.message}")
                    Toast.makeText(context, "Failed to subscribe", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: MqttException) {
            Log.e(TAG, "Exception while subscribing: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Publish message to control topic
     */
    fun publishMessage(message: String) {
        if (!mqttClient.isConnected) {
            Log.e(TAG, "Cannot publish, not connected")
            Toast.makeText(context, "MQTT not connected", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = 1
            mqttClient.publish(publishTopic, mqttMessage)
            Log.d(TAG, "Published message: $message to topic: $publishTopic")
            Toast.makeText(context, "Published to $publishTopic", Toast.LENGTH_SHORT).show()
        } catch (e: MqttException) {
            Log.e(TAG, "Publish error: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Disconnect from broker
     */
    fun disconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected from broker")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e(TAG, "Failed to disconnect: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }


}
