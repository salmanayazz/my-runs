package com.example.myruns

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
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
    private lateinit var myFragmentStateAdapter: MyFragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        tabFragments.add(startFragment)
        tabFragments.add(historyFragment)
        tabFragments.add(settingsFragment)

        myFragmentStateAdapter = MyFragmentStateAdapter(this, tabFragments)
        viewPager.adapter = myFragmentStateAdapter

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

    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}