package com.example.instagram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewPager = findViewById(R.id.viewPager)
        bottomNav = findViewById(R.id.bottomNav)

        // Set up ViewPager with fragments
        val adapter = viewpageradapter(this)
        viewPager.adapter = adapter

        // Sync ViewPager swipe with BottomNav
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNav.menu.getItem(position).isChecked = true
            }
        })

        // Sync BottomNav click with ViewPager page
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_search -> viewPager.currentItem = 1
                R.id.nav_post -> viewPager.currentItem = 2
                R.id.nav_notification -> viewPager.currentItem = 3
                R.id.nav_profile -> viewPager.currentItem = 4
            }
            true
        }
    }
}
