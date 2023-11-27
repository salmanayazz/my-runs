package com.example.myruns

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.myruns.ui.StartFragment
import com.example.myruns.ui.mapdisplay.MapDisplayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt
import weka.core.DenseInstance
import weka.core.Instance
import java.util.LinkedList
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CopyOnWriteArrayList


class TrackingService : Service(), LocationListener, SensorEventListener {
    companion object {
        val MSG_LOCATION_VALUE = 0
        val LOCATION_KEY = "location-key"

        val MSG_ACTIVITY_VALUE = 1
        val ACTIVITY_KEY = "activity-key"

        val DETECT_ACTIVITY = "detect-activity"
    }
    
    private val ACCELEROMETER_BLOCK_CAPACITY = 64

    private var messageHandler: Handler? = null
    private val CHANNEL_ID = "Tracking Notification"
    private val NOTIFY_ID = 1
    private var isReceiverRegistered = false
    private val locationManager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }
    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private val trackingBinder by lazy { TrackingBinder() }
    private var sensorManager: SensorManager? = null
    private val sensorReadingBuffer = mutableListOf<Double>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val fft = FFT(ACCELEROMETER_BLOCK_CAPACITY)
    private val bufferLock = Any()

    override fun onCreate() {
        checkPermission()
        isReceiverRegistered = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        checkPermission()
        startTracking()

        if (intent != null && intent.getBooleanExtra(DETECT_ACTIVITY, false)) {
            startActivityDetection()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return trackingBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        messageHandler = null
        return true
    }

    inner class TrackingBinder : Binder() {
        fun setMessageHandler(messageHandler: Handler) {
            this@TrackingService.messageHandler = messageHandler
        }
    }

    /**
     * shows a notification to the user that the app is tracking their exercise
     */
    private fun showNotification() {
        // launch MapViewActivity on notification click
        val intent = Intent(this, MapDisplayActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )

        notificationBuilder
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Recording your exercise")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.baseline_directions_run_24)
            .setContentIntent(pendingIntent) // click to open MapDisplayActivity
            .setOngoing(true) // make the notification undismissable

        val notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Tracking Notification",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(NOTIFY_ID, notification)
    }

    /**
     * checks if the app has the location permission
     * @throws SecurityException
     * if the app doesn't have the location permission or the build version is less than 23
     */
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Location permission not granted");
        }
    }

    /**
     * starts tracking the user's location
     */
    private fun startTracking() {
        try {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { return }

            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                this
            )

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * starts detecting the user's activity
     */
    private fun startActivityDetection() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        processSensorReadingBuffer()
    }

    /**
     * called automatically when the user's location changes
     * @param location
     * the user's new location
     */
    override fun onLocationChanged(location: Location) {
        Log.i("onLocationChanged", "lat: ${location.latitude}, long: ${location.longitude}")
        sendLocation(location)
    }

    /**
     * sends an location to the parent message handler
     */
    private fun sendLocation(location: Location) {
        if(messageHandler != null) {
            val bundle = Bundle()
            bundle.putParcelable(LOCATION_KEY, location)
            val message = messageHandler!!.obtainMessage()

            message.data = bundle
            message.what = MSG_LOCATION_VALUE
            messageHandler?.sendMessage(message)
        }
    }


    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0].toDouble()
        val y = event.values[1].toDouble()
        val z = event.values[2].toDouble()

        // add the magnitude to the buffer
        synchronized(bufferLock) {
            sensorReadingBuffer.add(sqrt(x * x + y * y + z * z))

            // keep buffer size at ACCELEROMETER_BLOCK_CAPACITY
            if (sensorReadingBuffer.size > ACCELEROMETER_BLOCK_CAPACITY) {
                sensorReadingBuffer.removeAt(0)
            }
        }
    }

    /**
     * processes the sensor reading buffer and classifies the activity
     */
    private fun processSensorReadingBuffer() {
        Thread {
            while (true) {
                // check if there are enough values in the buffer
                val values: List<Double>
                synchronized(bufferLock) {
                    values = if (sensorReadingBuffer.size == ACCELEROMETER_BLOCK_CAPACITY) {
                        sensorReadingBuffer.toList()
                    } else {
                        emptyList()
                    }
                }

                if (values.isNotEmpty()) {
                    val featureVector = generateFeatureArray(values)
                    try {
                        val detectedVal = WekaClassifier.classify(featureVector)
                        val activityType = abs(detectedVal - 2).toInt()
                        Log.i(TAG, "activity: ${StartFragment.activityTypeList[activityType]}")
                        sendActivity(activityType)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // Clear the buffer after processing
                    synchronized(bufferLock) {
                        sensorReadingBuffer.clear()
                    }
                }
            }
        }.start()
    }

    /**
     * generates a feature array from the sensor reading buffer
     * @param magnitude
     * the magnitude of the sensor readings
     * @return
     * the feature array
     */
    private fun generateFeatureArray(magnitude: List<Double>): Array<Any?> {
        val realNums = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)
        val imaginaryNums = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)

        val max = magnitude.maxOrNull() ?: 0.0

        for (i in realNums.indices) {
            realNums[i] = magnitude[i]
            imaginaryNums[i] = 0.0
        }

        fft.fft(realNums, imaginaryNums)

        val featureVector = arrayOfNulls<Any>(ACCELEROMETER_BLOCK_CAPACITY + 1)

        for (i in realNums.indices) {
            val mag = sqrt(realNums[i] * realNums[i] + imaginaryNums[i] * imaginaryNums[i])
            featureVector[i] = mag
            imaginaryNums[i] = 0.0
        }

        featureVector[ACCELEROMETER_BLOCK_CAPACITY] = max

        return featureVector
    }

    private fun sendActivity(activity: Int) {
        if(messageHandler != null) {
            val bundle = Bundle()
            bundle.putInt(ACTIVITY_KEY, activity)
            val message = messageHandler!!.obtainMessage()

            message.data = bundle
            message.what = MSG_ACTIVITY_VALUE
            messageHandler?.sendMessage(message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) {
            locationManager.removeUpdates(this)
        }

        sensorManager?.unregisterListener(this)

        notificationManager.cancel(NOTIFY_ID)
        coroutineScope.cancel()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}