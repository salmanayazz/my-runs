package com.example.myruns

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

object Utils {
    /**
     * returns the parcelable extra with the given name
     * @param name
     * the name of the parcelable extra
     * @param clazz
     * the class of the parcelable extra
     */
    fun <T> Intent.getParcelableExtraCompat(name: String, clazz: Class<T>? = null): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && clazz != null) {
            getParcelableExtra(name, clazz)
        } else {
            getParcelableExtra(name)
        }
    }

    /**
     * Returns the parcelable extra with the given name.
     * @param name the name of the parcelable extra
     * @param clazz the class of the parcelable extra
     */
    fun <T : Parcelable> Intent.getParcelableArrayListCompat(name: String, clazz: Class<T>? = null): ArrayList<out T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && clazz != null) {
            getParcelableArrayListExtra(name, clazz)
        } else {
            getParcelableArrayListExtra(name)
        }
    }

    /**
     * Returns the parcelable extra with the given name.
     * @param name the name of the parcelable extra
     * @param clazz the class of the parcelable extra
     */
    fun <T : Parcelable> Bundle.getParcelableCompat(name: String, clazz: Class<T>? = null): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && clazz != null) {
            getParcelable(name, clazz)
        } else {
            getParcelable(name)
        }
    }
}