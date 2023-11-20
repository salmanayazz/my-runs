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
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.myruns.ui.mapdisplay.MapDisplayActivity


class TrackingService : Service(), LocationListener {
    companion object {
        val LOCATION_EVENT = "exercise-entry-event"
        val LOCATION_KEY = "exercise-entry-key"

        val STOP_SERVICE_ACTION = "stop-service-action"
    }

    private val CHANNEL_ID = "Tracking Notification"
    private val NOTIFY_ID = 1
    private val stopServiceReceiver by lazy { StopServiceReceiver() }
    private var isReceiverRegistered = false
    private val locationManager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }
    val gmsLocationList = ArrayList<Location>()
    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreate() {
        checkPermission()
        val filter = IntentFilter(STOP_SERVICE_ACTION)
        registerReceiver(stopServiceReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        checkPermission()
        startTracking()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

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
        gmsLocationList.add(location)
        sendLocation()
    }
    
    /**
     * sends an location ArrayList to the parent activity/fragment
     */
    private fun sendLocation() {
        val intent = Intent().apply {
            action = LOCATION_EVENT
            putExtra(LOCATION_KEY, gmsLocationList)
        }

        sendBroadcast(intent)
    }

   inner class StopServiceReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == STOP_SERVICE_ACTION) {
                // stop this service when parent activity/fragment sends a broadcast to stop
                stopSelf()
                unregisterReceiver(stopServiceReceiver)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) {
            locationManager.removeUpdates(this)
        }
        notificationManager.cancel(NOTIFY_ID)
    }

    // 3 functions to stop fix crashing issue on api 24
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}