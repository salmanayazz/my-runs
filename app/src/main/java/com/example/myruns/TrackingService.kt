package com.example.myruns

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.RemoteException
import androidx.core.content.ContextCompat
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.StartFragment
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.util.Calendar

interface ExerciseEntryListener {
    fun send(exerciseEntry: ExerciseEntry)
}

class TrackingService : Service(), LocationListener {
    companion object {
        val EXERCISE_ENTRY_EVENT = "tracking-service-exercise-entry-event"
        val EXERCISE_ENTRY_KEY = "tracking-service-exercise-entry-key"
    }


    private lateinit var exerciseRepository: ExerciseRepository
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
        setupRepository()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTracking()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Location permission not granted");
        }
    }

    private fun setupRepository() {
        val database = ExerciseDatabase.getInstance(applicationContext)
        val databaseDao = database.exerciseDatabaseDao
        exerciseRepository = ExerciseRepository(databaseDao)
    }

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

    override fun onLocationChanged(location: Location) {
        println("onlocationchanged: ${location.latitude} ${location.longitude}")
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)

        println("onLocationChanged: editingExerciseEntry value = ${editingExerciseEntry.locationList}")
        editingExerciseEntry.locationList?.add(latLng)
        sendExerciseEntryToActivity(editingExerciseEntry)

    }

    private fun sendExerciseEntryToActivity(exerciseEntry: ExerciseEntry) {
        val intent = Intent().apply {
            action = EXERCISE_ENTRY_EVENT
            putExtra(EXERCISE_ENTRY_KEY, exerciseEntry)
        }
        sendBroadcast(intent)
    }
}