package com.mubdiur.webcurator.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mubdiur.webcurator.adapter.PagerAdapter
import com.mubdiur.webcurator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val pages = arrayOf("Home", "Feeds", "Browser")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /**
         *  Hides the default actionbar at top
         * */
        supportActionBar?.hide()

        binding.viewPager.adapter = PagerAdapter(this)

        /**
         * Attached Viewpager with tab bar
         * */
        TabLayoutMediator(binding.tabBar, binding.viewPager) { tab, position ->
            tab.text = pages[position]
        }.attach()

        /**
         * Change top bar according to pages
         * */
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                binding.titleText.text = pages[position]
//
//                /**
//                 * Add Button For Feeds Page
//                 * */
//                if(position==1) {
//                    binding.addButton.visibility = View.VISIBLE
//                } else {
//                    binding.addButton.visibility = View.INVISIBLE
//                }
                when (position) {
                    0 -> {
                        /**
                         * Home Page
                         * */
                        // Hide stuff for Feeds and Browser
                        binding.addButton.visibility = View.INVISIBLE
                        // Show stuff for Home
                        binding.titleText.visibility = View.VISIBLE
                        binding.titleText.text = pages[position]

                    }
                    1 -> {
                        /**
                         * Feeds Page
                         * */
                        // Hide stuff for Home and Browser

                        // Show stuff for Feeds
                        binding.titleText.visibility = View.VISIBLE
                        binding.titleText.text = pages[position]
                        binding.addButton.visibility = View.VISIBLE
                    }
                    2 -> {
                        /**
                         * Browser Page
                         * */
                        // Hide stuff for Home and Feeds
                        binding.titleText.visibility = View.GONE
                        binding.addButton.visibility = View.INVISIBLE
                        // Show stuff for Browser
                    }
                } // end of when
            } // end of on page selected
        }) // end of registerOnPageChangeCallback
    } // end of onCreate
}