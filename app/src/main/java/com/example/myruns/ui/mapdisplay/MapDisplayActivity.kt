package com.example.myruns.ui.mapdisplay

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myruns.ExerciseEntryListener
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myruns.databinding.ActivityMapDisplayBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback {
    private val FINE_LOCATION_REQUEST_CODE = 0

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapDisplayBinding
    private lateinit var mapDisplayViewModel: MapDisplayViewModel

    private lateinit var  markerOptions: MarkerOptions
    private lateinit var serviceMessenger: Messenger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()

        // setup mvvm objects
        val database = ExerciseDatabase.getInstance(applicationContext)
        val databaseDao = database.exerciseDatabaseDao
        val repository = ExerciseRepository(databaseDao)
        val viewModelFactory = ExerciseViewModelFactory(repository)
        mapDisplayViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[MapDisplayViewModel::class.java]



        findViewById<Button>(R.id.confirm).setOnClickListener() {
            finish()
        }
        findViewById<Button>(R.id.cancel).setOnClickListener() {
            finish()
        }


        markerOptions = MarkerOptions()
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
            }
        }
    }



    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            throw SecurityException("test")
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_REQUEST_CODE)
        } else {
            startTrackingService()
        }
    }

    private fun startTrackingService() {
        val serviceIntent = Intent(this, TrackingService::class.java)
        this.startService(serviceIntent)

        val filter = IntentFilter(TrackingService.EXERCISE_ENTRY_EVENT)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("on received")
            val byteArrayExtra = intent?.getByteArrayExtra(TrackingService.EXERCISE_ENTRY_KEY)
            if (byteArrayExtra != null) {
                val byteArrayInputStream = ByteArrayInputStream(byteArrayExtra)
                val objectInputStream = ObjectInputStream(byteArrayInputStream)
                try {
                    val editingExerciseEntry = objectInputStream.readObject() as? ExerciseEntry

                    lifecycleScope.launch {
                        // Handle the exerciseEntry here

                        if (editingExerciseEntry != null) {
                            if (editingExerciseEntry.locationList != null) {
                                val cameraUpdate =
                                    CameraUpdateFactory.newLatLngZoom(editingExerciseEntry.locationList.last(), 17f)
                                mMap.animateCamera(cameraUpdate)
                                markerOptions.position(editingExerciseEntry.locationList.last())
                                mMap.addMarker(markerOptions)
                            }
                        }
                    }
                    // Use the receivedExerciseEntry as needed
                } catch(e: Exception) {
                    e.printStackTrace()
                } finally {
                    objectInputStream.close()
                    byteArrayInputStream.close()
                }


            }
        }
    }

    private val exerciseEntryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == TrackingService.EXERCISE_ENTRY_EVENT) {
                    val exerciseEntry = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelableExtra(
                            TrackingService.EXERCISE_ENTRY_KEY,
                            ExerciseEntry::class.java
                        )
                    } else {
                        it.getParcelableExtra(TrackingService.EXERCISE_ENTRY_KEY)
                    }

                    onExerciseEntryReceived(exerciseEntry)
                }
            }
        }
    }

    private fun onExerciseEntryReceived(exerciseEntry: ExerciseEntry?) {
        lifecycleScope.launch {
            // Handle the exerciseEntry here

            if (exerciseEntry != null) {
                if (exerciseEntry.locationList != null) {
                    val cameraUpdate =
                        CameraUpdateFactory.newLatLngZoom(exerciseEntry.locationList.last(), 17f)
                    mMap.animateCamera(cameraUpdate)
                    markerOptions.position(exerciseEntry.locationList.last())
                    mMap.addMarker(markerOptions)
                }
            }
        }
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(TrackingService.EXERCISE_ENTRY_EVENT)
        registerReceiver(exerciseEntryReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(exerciseEntryReceiver)
    }
}