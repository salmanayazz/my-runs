package com.example.salman_ayaz_myruns

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val profilePhoto = MutableLiveData<Bitmap>()
    var isDialogShowing: Boolean = false
}