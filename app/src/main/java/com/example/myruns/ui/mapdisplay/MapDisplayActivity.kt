package com.example.myruns.ui.mapdisplay

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.preference.PreferenceManager
import com.example.myruns.Utils.getParcelableArrayListCompat
import com.example.myruns.ui.StartFragment
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar
import com.example.myruns.Utils.getParcelableExtraCompat
import com.example.myruns.ui.SettingsFragment
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.VisibleRegion
import java.text.DecimalFormat

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

    private val exerciseStats by lazy { findViewById<TextView>(R.id.exercise_stats) }
    private var inputType: String = StartFragment.inputTypeList[0]
    private var activityType: Int = 0
    private var staticMap = false
    private var exerciseEntry: ExerciseEntry? = null
    private var gmsLocationList = ArrayList<Location>()
    private var isMetric = true
    private val metersPerSecToKmPerHour = 3.6
    private var hasStarted = false
    private val timeStarted = Calendar.getInstance().timeInMillis
    private lateinit var appContext: Context
    private var trackerIsBinded = false
    private val BIND_STATUS_KEY = "bind-status-key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUnit()

        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupMVVM()
        setupListeners()

        exerciseEntry = intent.getParcelableExtraCompat(
            EXERCISE_ENTRY_KEY,
            ExerciseEntry::class.java
        )

        if (exerciseEntry != null) {
            // if exerciseEntry passed then show static map
            // exerciseEntry gets displayed in "onMapReady" func
            staticMap = true
            setStatic()
            return
        }

        val intentInputType = intent.getStringExtra(INPUT_TYPE_KEY)

        if (intentInputType != null) {
            inputType = intentInputType
        }
        activityType = intent.getIntExtra(ACTIVITY_TYPE_KEY, -1)

        appContext = this.applicationContext

        checkPermission()

        if(savedInstanceState != null) {
            trackerIsBinded = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }

    }

    /**
     * checks the unit preference in the settings
     * and sets the isMetric variable accordingly
     */
    private fun checkUnit() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val unit = sharedPreferences
            .getString(
                SettingsFragment.UNIT_PREFERENCE,
                SettingsFragment.UNIT_METRIC
            )
        isMetric = unit == SettingsFragment.UNIT_METRIC
    }

    /**
     * sets the activity to static mode by removing the
     * confirm and cancel buttons and showing the delete button
     */
    private fun setStatic() {
        findViewById<AppCompatImageView>(R.id.delete_entry).visibility = View.VISIBLE
        findViewById<FlexboxLayout>(R.id.button_layout).visibility = View.GONE
    }


    /**
     * handles the result of the permission request
     * if the permission is granted then start the tracking service
     * if the permission is denied then finish the activity
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FINE_LOCATION_REQUEST_CODE) { // location permissions
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // granted
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

    /**
     * checks if the app has the location permission
     */
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

    /**
     * sets up the listeners for the confirm, cancel, and delete buttons
     */
    private fun setupListeners() {
        findViewById<Button>(R.id.confirm).setOnClickListener() {
            if (exerciseEntry != null) {
                mapDisplayViewModel.insert(exerciseEntry!!)
            }
            finish()
        }
        findViewById<Button>(R.id.cancel).setOnClickListener() {
            finish()
        }
        findViewById<AppCompatImageView>(R.id.delete_entry).setOnClickListener() {
            if (exerciseEntry != null && staticMap) {
                mapDisplayViewModel.delete(exerciseEntry!!.id)

            }
            finish()
        }
    }

    /**
     * starts the tracking service and binds it to this activity
     */
    private fun startTrackingService() {
        println("inputType ${inputType} & ${inputType == StartFragment.INPUT_TYPE_AUTOMATIC}")
        intent = Intent(this, TrackingService::class.java)
            .putExtra(TrackingService.DETECT_ACTIVITY, inputType == StartFragment.INPUT_TYPE_AUTOMATIC)
        this.startService(intent)
        hasStarted = true
        bindService()

        mapDisplayViewModel.gmsLocationList.observe(this) {
            if (it == null || it.isEmpty()) { return@observe }
            exerciseEntry = gmsLocationToExerciseActivity(it)

            updateMap(exerciseEntry)
            updateExerciseStats(exerciseEntry)
        }

        mapDisplayViewModel.detectedActivity.observe(this) {
            activityType = it
        }
    }

    /**
     * converts a list of google maps service locations to an ExerciseEntry
     * will calculate the distance, avgPace, climb, and calories
     * @param gmsLocationList
     * the list of google maps service locations to convert
     * @return
     * the ExerciseEntry converted from the given list of google maps service locations
     */
    private fun gmsLocationToExerciseActivity(gmsLocationList: ArrayList<Location>): ExerciseEntry {
        val avgPace = if (gmsLocationList.isNotEmpty()) {
            val totalSpeed = gmsLocationList.sumOf { it.speed.toDouble() }
            totalSpeed / gmsLocationList.size * metersPerSecToKmPerHour // convert from m/s to k/h
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

        // estimate calories burned
        val calories = (distance * 0.035)

        return  ExerciseEntry(
            0,
            inputType,
            activityType,
            Calendar.getInstance(),
            (Calendar.getInstance().timeInMillis - timeStarted).toDouble() / (1000 * 60), // convert milisec to mins
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
            if (exerciseEntry == null || !::mMap.isInitialized) {
                return@launch
            }

            val locationList = exerciseEntry.locationList

            if (locationList != null && locationList.size >= 2) {
                // clear prev data
                mMap.clear()

                // markers for beginning and end
                val startMarker = MarkerOptions().position(locationList.first()).title("Start")
                val endMarker = MarkerOptions().position(locationList.last()).title("End")
                mMap.addMarker(startMarker)
                mMap.addMarker(endMarker)

                // draw polyline for all other locations
                val polylineOptions = PolylineOptions().addAll(locationList)
                mMap.addPolyline(polylineOptions)

                // center camera only if markers are off the screen
                val visibleRegion = mMap.projection.visibleRegion
                if (!isMarkerInBounds(startMarker.position, visibleRegion) ||
                    !isMarkerInBounds(endMarker.position, visibleRegion)
                ) {
                    val boundsBuilder = LatLngBounds.builder()
                        .include(startMarker.position)
                        .include(endMarker.position)

                    val bounds = boundsBuilder.build()

                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50)
                    mMap.animateCamera(cameraUpdate)
                }
            }
        }
    }

    /**
     * checks if the given marker is in the given visible region
     */
    private fun isMarkerInBounds(markerPosition: LatLng, visibleRegion: VisibleRegion): Boolean {
        return markerPosition.latitude > visibleRegion.latLngBounds.southwest.latitude &&
                markerPosition.latitude < visibleRegion.latLngBounds.northeast.latitude &&
                markerPosition.longitude > visibleRegion.latLngBounds.southwest.longitude &&
                markerPosition.longitude < visibleRegion.latLngBounds.northeast.longitude
    }


    /**
     * updates the exercise stats TextView with the data from the given ExerciseEntry
     * @param exerciseEntry
     * the ExerciseEntry to update the exercise stats TextView with
     */
    private fun updateExerciseStats(exerciseEntry: ExerciseEntry?) {
        if (exerciseEntry == null) { return }

        // build the string based on different parameter, like unit preference, static map, etc.
        val decimalFormat = DecimalFormat("#.#######")

        var unitMultiply = 1.0
        var unit = "kilometer"
        var unitPerHour = "km/h"

        if (!isMetric) {
            unitMultiply = SettingsFragment.KM_TO_MILES
            unit = "miles"
            unitPerHour = "m/h"
        }

        var text = if (activityType < 0 || activityType > StartFragment.activityTypeList.size) {
            "Activity Type: Unknown\n"
        } else {
            "Activity Type: ${StartFragment.activityTypeList[activityType]}\n"
        }

        val formattedAvgSpeed = decimalFormat.format(exerciseEntry.avgPace?.times(unitMultiply))

        text += "Average Speed: $formattedAvgSpeed $unitPerHour\n"

        // don't show current speed if map is static
        if (!staticMap && gmsLocationList.isNotEmpty()) {
            val formattedCurrentSpeed = decimalFormat.format(gmsLocationList.last().speed * metersPerSecToKmPerHour * unitMultiply)

            text += "Current Speed: $formattedCurrentSpeed $unitPerHour\n"
        }

        val formattedClimb = decimalFormat.format(exerciseEntry.climb)
        val formattedCalories = decimalFormat.format(exerciseEntry.calories)
        val formattedDistance = decimalFormat.format(exerciseEntry.distance)

        text += "Climb: $formattedClimb $unit\n" +
                "Calories: $formattedCalories\n" +
                "Distance: $formattedDistance $unit"

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

        updateMap(exerciseEntry)
        updateExerciseStats(exerciseEntry)
    }

    override fun onResume() {
        super.onResume()

        if (!staticMap && hasStarted) {
            checkPermission()
        }
    }

    /**
     * binds the tracking service to this activity
     * if the service is already binded then do nothing
     */
    private fun bindService() {
        if (!trackerIsBinded) {
            appContext.bindService(intent, mapDisplayViewModel, Context.BIND_AUTO_CREATE)
            trackerIsBinded = true
        }
    }

    /**
     * unbinds the tracking service from this activity
     * if the service is not binded then do nothing
     */
    private fun unBindService(){
        if (trackerIsBinded) {
            appContext.unbindService(mapDisplayViewModel)
            trackerIsBinded = false
        }
    }

    /**
     * saves the bind status of the tracking service
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BIND_STATUS_KEY, trackerIsBinded)
    }

    override fun onDestroy() {
        super.onDestroy()
        unBindService()
        this.stopService(intent)
    }
}