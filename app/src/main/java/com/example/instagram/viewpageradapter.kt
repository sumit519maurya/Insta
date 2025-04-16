package com.example.instagram

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class viewpageradapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Homef()
            1 -> searchf()
            2 -> postf()
            3 -> notificationf()
            4 -> profilef()
            else -> Homef()
        }
    }
}