package no.jmheiberg.jonma.note2self.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log


//This class is inspired by https://demonuts.com/android-shake-detection/ &
// https://developer.android.com/guide/components/services
const val BROADCAST_ACTION = "no.jmheiberg.jonma.note2self.BROADCAST"

class SensorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private var accel: Float = 0f
    private var accelLast: Float = 0f
    private var accelCurrent: Float = 0f

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, Handler())
        return START_STICKY
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        accelLast = accelCurrent
        accelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta = accelCurrent - accelLast
        accel = accel.times(0.9f) + delta

        if (accel > 11) {
            val intent = Intent(BROADCAST_ACTION).apply {
                putExtra("Shake", accel)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        }
    }

}


