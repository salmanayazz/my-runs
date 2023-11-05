package com.example.salman_ayaz_myruns.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * adapter for the tab layout on MainActivity
 */
class TabFragmentStateAdapter(activity: FragmentActivity, private var list: ArrayList<Fragment>) :
    FragmentStateAdapter(activity){

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }
}