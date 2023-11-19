package com.example.myruns

import android.content.Intent
import android.os.Build

object Utils {
    fun <T> Intent.getParcelableExtraCompat(name: String, clazz: Class<T>? = null): T? {
        /**
         * returns the parcelable extra with the given name
         * @param name
         * the name of the parcelable extra
         * @param clazz
         * the class of the parcelable extra
         */
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && clazz != null) {
            getParcelableExtra(name, clazz)
        } else {
            getParcelableExtra(name)
        }
    }
}