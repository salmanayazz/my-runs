package com.example.myruns.ui.mapdisplay

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.data.exercise.ExerciseViewModelFactory
import com.example.myruns.TrackingService
import com.example.myruns.data.exercise.ExerciseEntry
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myruns.databinding.ActivityMapDisplayBinding
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch
import android.location.Location
import com.example.myruns.ui.StartFragment
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val INPUT_TYPE_KEY = "input-type-key"
        const val ACTIVITY_TYPE_KEY = "activity-type-key"
        const val EXERCISE_ENTRY_KEY = "exercise-entry-key"
    }

    private val FINE_LOCATION_REQUEST_CODE = 0

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapDisplayBinding
    private lateinit var mapDisplayViewModel: MapDisplayViewModel
    private lateinit var  markerOptions: MarkerOptions
    private lateinit var  polylineOptions: PolylineOptions
    private var mapCentered = false

    private val gmsLocationList = ArrayList<Location>()
    private val exerciseStats by lazy { findViewById<TextView>(R.id.exercise_stats) }

    private var inputType: String = StartFragment.inputTypeList[0]
    private var activityType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()

        setupMVVM()

        findViewById<Button>(R.id.confirm).setOnClickListener() {
            finish()
        }
        findViewById<Button>(R.id.cancel).setOnClickListener() {
            finish()
        }

        val intentInputType = intent.getStringExtra(INPUT_TYPE_KEY)

        if (intentInputType != null) {
            inputType = intentInputType
        }
        activityType = intent.getIntExtra(ACTIVITY_TYPE_KEY, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FINE_LOCATION_REQUEST_CODE) { // location permissions
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // granted
                println("permission granted")
                startTrackingService()
            } else { // denied
                Toast.makeText(
                    this,
                    "Please enable location permissions",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_REQUEST_CODE)
        } else {
            startTrackingService()
        }
    }

    private fun setupMVVM() {
        val database = ExerciseDatabase.getInstance(applicationContext)
        val databaseDao = database.exerciseDatabaseDao
        val repository = ExerciseRepository(databaseDao)
        val viewModelFactory = ExerciseViewModelFactory(repository)
        mapDisplayViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[MapDisplayViewModel::class.java]
    }

    private fun startTrackingService() {
        val serviceIntent = Intent(this, TrackingService::class.java)
        this.startService(serviceIntent)
    }

    private fun stopTrackingService() {
        val intent = Intent()
        intent.action = TrackingService.STOP_SERVICE_ACTION
        sendBroadcast(intent)
    }

    /**
     * the receiver for ExerciseEntry events from the TrackingService
     */
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == TrackingService.LOCATION_EVENT) {
                    val gmsLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelableExtra(
                            TrackingService.LOCATION_KEY,
                            Location::class.java
                        )
                    } else {
                        it.getParcelableExtra(TrackingService.LOCATION_KEY)
                    }

                    if (gmsLocation == null) { return }

                    gmsLocationList.add(gmsLocation)
                    val exerciseEntry = gmsLocationToExerciseActivity(gmsLocationList)

                    updateMap(exerciseEntry)
                    updateExerciseStats(exerciseEntry)
                }
            }
        }
    }

    private fun gmsLocationToExerciseActivity(gmsLocationList: ArrayList<Location>): ExerciseEntry {
        val avgPace = if (gmsLocationList.isNotEmpty()) {
            val totalSpeed = gmsLocationList.sumOf { it.speed.toDouble() }
            totalSpeed / gmsLocationList.size * 3.6 // convert from m/s to k/h
        } else {
            0.0
        }

        val locationList = ArrayList<LatLng>(gmsLocationList.map {
            LatLng(it.latitude, it.longitude)
        })

        val climb = (gmsLocationList.first().altitude - gmsLocationList.last().altitude) / 1000

        var lastGmsLocation = gmsLocationList.firstOrNull()
        val distance = gmsLocationList.drop(1).fold(0.0) { total, currentLocation ->
            lastGmsLocation?.let { lastLocation ->
                val result = FloatArray(1)
                Location.distanceBetween(
                    lastLocation.latitude,
                    lastLocation.longitude,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    result
                )
                lastGmsLocation = currentLocation
                total + (result[0] / 1000) // convert from m to km
            } ?: total
        }

        val calories = (distance * 0.035)

        return  ExerciseEntry(
            0,
            inputType,
            0,
            Calendar.getInstance(),
            null,
            distance,
            avgPace,
            calories,
            climb,
            null,
            null,
            locationList
        )
    }


    /**
     * updates the map with the data from the given ExerciseEntry
     * @param exerciseEntry
     * the ExerciseEntry to update the map with
     */
    private fun updateMap(exerciseEntry: ExerciseEntry?) {
        lifecycleScope.launch {
            if (exerciseEntry != null) {
                val locationList = exerciseEntry.locationList

                if (locationList != null && locationList.size >= 2) {
                    // clear prev data
                    mMap.clear()

                    // markers for beginning and end
                    mMap.addMarker(MarkerOptions().position(locationList.first()).title("Start"))
                    mMap.addMarker(MarkerOptions().position(locationList.last()).title("End"))

                    // draw polyline for all other locations
                    val polylineOptions = PolylineOptions().addAll(locationList)
                    mMap.addPolyline(polylineOptions)

                    // center camera
                    if (!mapCentered) {
                        val cameraUpdate =
                            CameraUpdateFactory.newLatLngZoom(locationList.last(), 100f)
                        mMap.animateCamera(cameraUpdate)
                        mapCentered = true
                    }
                }
            }
        }
    }

    /**
     *
     */
    private fun updateExerciseStats(exerciseEntry: ExerciseEntry?) {
        if (exerciseEntry == null) { return }

        var text = if (inputType == StartFragment.INPUT_TYPE_AUTOMATIC) {
            "Activity Type: Unknown\n"
        } else {
            "Activity Type: ${StartFragment.activityTypeList[activityType]}\n"
        }

        text += "Average Speed: ${exerciseEntry.avgPace} km/h\n" +
                "Current Speed: ${gmsLocationList.last().speed * 3.6} km/h\n" +
                "Climb: ${exerciseEntry.climb} km\n" +
                "Calories: ${exerciseEntry.calories}\n" +
                "Distance: ${exerciseEntry.distance} km"

        exerciseStats.text = text
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onResume() {
        super.onResume()
        // restart the tracking service receiver
        val filter = IntentFilter(TrackingService.LOCATION_EVENT)
        registerReceiver(locationReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        // stop the tracking service receiver
        unregisterReceiver(locationReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTrackingService()
    }
}