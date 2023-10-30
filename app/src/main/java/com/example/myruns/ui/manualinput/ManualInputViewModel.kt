package com.example.myruns.ui.manualinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.sql.Time
import java.util.Date

class ManualInputViewModel(): ViewModel() {
    enum class DialogOptions {
        DURATION,
        DISTANCE,
        CALORIES,
        HEART_RATE,
        COMMENTS
    }
    var dialogOpen = MutableLiveData<DialogOptions>(null)

    var date = MutableLiveData<String>()
    var time = MutableLiveData<String>()

    var duration = MutableLiveData<Int>()
    var distance = MutableLiveData<Int>()
    var calories = MutableLiveData<Int>()
    var heartRate = MutableLiveData<Int>()
    var comments = MutableLiveData<String>()


}