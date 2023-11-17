package com.example.myruns

import android.Manifest
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
import android.os.Message
import android.os.RemoteException
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.StartFragment
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.util.Calendar

class TrackingService : Service(), LocationListener {
    companion object {
        val EXERCISE_ENTRY_EVENT = "exercise-entry-event"
        val EXERCISE_ENTRY_KEY = "exercise-entry-key"

        val STOP_SERVICE_ACTION = "stop-service-action"
    }


    private val stopServiceReceiver by lazy { StopServiceReceiver() }
    private lateinit var locationManager: LocationManager
    private var editingExerciseEntry: ExerciseEntry = ExerciseEntry(
        0,
        StartFragment.INPUT_TYPE_MANUAL, // TODO: change
        0,
        Calendar.getInstance(),
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        "",
        locationList = ArrayList()
    )

    override fun onCreate() {
        checkPermission()

        val filter = IntentFilter(STOP_SERVICE_ACTION)
        registerReceiver(stopServiceReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTracking()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * checks if the app has the location permission
     * @throws SecurityException
     * if the app doesn't have the location permission or the build version is less than 23
     */
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            throw SecurityException("Unsupported Build Version");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Location permission not granted");
        }
    }

    /**
     * starts tracking the user's location
     */
    private fun startTracking() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

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
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)

        editingExerciseEntry.locationList?.add(latLng)

        sendExerciseEntry(editingExerciseEntry)
    }
    
    /**
     * sends an ExerciseEntry to the parent activity/fragment
     */
    private fun sendExerciseEntry(exerciseEntry: ExerciseEntry) {
        val intent = Intent().apply {
            action = EXERCISE_ENTRY_EVENT
            putExtra(EXERCISE_ENTRY_KEY, exerciseEntry)
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
    }
}