package com.example.myruns.ui.mapdisplay

import android.content.ComponentName
import android.content.ServiceConnection
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myruns.TrackingService
import com.example.myruns.Utils.getParcelableCompat
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.SettingsFragment

class MapDisplayViewModel(
    private val repository: ExerciseRepository
): ViewModel(), ServiceConnection {
    private var trackingMessageHandler: TrackingMessageHandler = TrackingMessageHandler(Looper.getMainLooper())

    // the list of locations from the tracking service
    private val _gmsLocationList = MutableLiveData<ArrayList<Location>>()
    val gmsLocationList: LiveData<ArrayList<Location>>
        get() = _gmsLocationList

    inner class TrackingMessageHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            // if the message is a location value, add it to the list
            if (msg.what == TrackingService.MSG_LOCATION_VALUE) {
                val bundle = msg.data
                val location = bundle.getParcelableCompat(TrackingService.LOCATION_KEY, Location::class.java)

                if (location != null && _gmsLocationList.value != null) {
                    _gmsLocationList.value = _gmsLocationList.value?.apply { add(location) }
                } else if (location != null) {
                    _gmsLocationList.value = ArrayList()
                }
            }
        }
    }

    fun insert(exerciseEntry: ExerciseEntry) {
        // the unit type is always metric
        repository.insert(exerciseEntry, SettingsFragment.UNIT_METRIC)
    }

    fun delete(id: Long) {
        repository.delete(id)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val trackingBinder = service as TrackingService.TrackingBinder
        trackingBinder.setMessageHandler(trackingMessageHandler)
    }

    override fun onServiceDisconnected(name: ComponentName?) {}
}