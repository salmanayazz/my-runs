package com.example.myruns

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
import com.example.myruns.ui.mapdisplay.MapDisplayActivity


class TrackingService : Service(), LocationListener {
    companion object {
        val MSG_LOCATION_VALUE = 0
        val LOCATION_KEY = "location-key"
    }

    private var messageHandler: Handler? = null
    private val CHANNEL_ID = "Tracking Notification"
    private val NOTIFY_ID = 1
    private var isReceiverRegistered = false
    private val locationManager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }
    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private val trackingBinder by lazy { TrackingBinder() }

    override fun onCreate() {
        checkPermission()
        isReceiverRegistered = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        checkPermission()
        startTracking()

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
            Log.i("Tracking Service", "setMessageHandler has been called")
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

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) {
            locationManager.removeUpdates(this)
        }
        notificationManager.cancel(NOTIFY_ID)
    }
}