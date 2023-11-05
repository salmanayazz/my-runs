package com.example.salman_ayaz_myruns.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * view model for the ProfileActivity
 */
class ProfileViewModel : ViewModel() {
    val profilePhoto = MutableLiveData<Bitmap>()
    var isDialogShowing: Boolean = false
}