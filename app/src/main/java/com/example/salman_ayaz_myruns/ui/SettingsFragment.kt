package com.example.salman_ayaz_myruns.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.salman_ayaz_myruns.R
import com.example.salman_ayaz_myruns.ui.profile.ProfileActivity

/**
 * fragment that displays the settings screen and handles the user's settings
 * @see R.xml.settings_preference
 */
class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var sharedPreferences: SharedPreferences
    companion object {
        const val SETTINGS_PREF_KEY = "settings_pref_key" // key for the settings shared preferences
        const val UNIT_PREFERENCE = "unit_preference"
        const val UNIT_METRIC = "metric"
        const val UNIT_IMPERIAL = "imperial"
        const val PROFILE_PREFERENCE = "user_profile"
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        sharedPreferences = requireContext().getSharedPreferences(SETTINGS_PREF_KEY, Context.MODE_PRIVATE)

        // click listener that opens ProfileActivity
        val userProfilePreference = findPreference<Preference>(PROFILE_PREFERENCE)
        userProfilePreference?.setOnPreferenceClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
            true
        }

    }
}