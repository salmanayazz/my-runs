package com.example.myruns.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val profilePhoto = MutableLiveData<Bitmap>()
    var isDialogShowing: Boolean = false
}