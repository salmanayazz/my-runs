package com.example.myruns

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myruns.ui.history.HistoryFragment
import com.example.myruns.ui.TabFragmentStateAdapter
import com.example.myruns.ui.SettingsFragment
import com.example.myruns.ui.StartFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val tabFragments = ArrayList<Fragment>()
    private val startFragment = StartFragment()
    private val historyFragment = HistoryFragment()
    private val settingsFragment = SettingsFragment()

    private val tabLayout by lazy { findViewById<TabLayout>(R.id.tab_layout) }
    private val viewPager by lazy { findViewById<ViewPager2>(R.id.view_pager) }
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabFragmentStateAdapter: TabFragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        // set up tab layout
        tabFragments.add(startFragment)
        tabFragments.add(historyFragment)
        tabFragments.add(settingsFragment)

        tabFragmentStateAdapter = TabFragmentStateAdapter(this, tabFragments)
        viewPager.adapter = tabFragmentStateAdapter

        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Start"
                    tab.setIcon(R.drawable.baseline_directions_run_24)
                }
                1 -> {
                    tab.text = "History"
                    tab.setIcon(R.drawable.baseline_history_24)
                }
                2 -> {
                    tab.text = "Settings"
                    tab.setIcon(R.drawable.baseline_settings_24)
                }
            }
        }
        tabLayoutMediator.attach()

        // request notification permissions
        val notificationPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }

        notificationPermissionLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}