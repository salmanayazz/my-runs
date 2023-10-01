package com.example.salman_ayaz_myruns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private var tabFragments = ArrayList<Fragment>()
    private val startRunsFragment = StartRunsFragment()
    private val historyFragment = HistoryFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        tabFragments.add(startRunsFragment)
        tabFragments.add(historyFragment)
        tabFragments.add(settingsFragment)

    }
}