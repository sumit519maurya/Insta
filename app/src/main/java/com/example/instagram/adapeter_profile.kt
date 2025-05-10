package com.example.instagram

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class adapeter_profile (
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> post_frag()
            1 -> reel_frag()
            2 -> tag_frag()
            else -> post_frag()
        }
    }
}