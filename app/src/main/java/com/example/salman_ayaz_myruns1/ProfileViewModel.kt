package com.example.salman_ayaz_myruns1

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val profilePhoto = MutableLiveData<Bitmap>()
}