package com.mubdiur.webcurator.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mubdiur.webcurator.adapter.PagerAdapter
import com.mubdiur.webcurator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val pages = arrayOf("Home", "Feed", "Browser")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /**
         *  Hides the default actionbar at top
         *
         * */
        supportActionBar?.hide()

        binding.viewPager.adapter = PagerAdapter(this)

        /**
         * Attached Viewpager with tab bar
         *
         * */
        TabLayoutMediator(binding.tabBar, binding.viewPager) { tab, position ->
            tab.text = pages[position]
        }.attach()

        /**
         * Change top bar according to pages
         *
         * */
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.titleText.text = pages[position]
            }
        })

    }
}