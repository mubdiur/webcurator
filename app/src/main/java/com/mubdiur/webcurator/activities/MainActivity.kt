package com.mubdiur.webcurator.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.adapters.PagerAdapter
import com.mubdiur.webcurator.databases.DatabaseClient
import com.mubdiur.webcurator.databinding.ActivityMainBinding
import com.mubdiur.webcurator.fragments.FeedNameFragment
import com.mubdiur.webcurator.interfaces.OnBackPressed


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val pages = arrayOf("Home", "Feeds", "Browser")

    @SuppressLint("SetTextI18n")
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
        binding.viewPager.isUserInputEnabled = false
        /**
         * Attached Viewpager with tab bar
         * */
        TabLayoutMediator(binding.tabBar, binding.viewPager) { tab, position ->
            tab.text = pages[position]
        }.attach()

        DatabaseClient.getInstance(this.applicationContext)
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
                        if (supportFragmentManager.backStackEntryCount == 0)
                            binding.titleText.text = pages[position]
                        else
                            binding.titleText.text = "Create Feed"

                        if (supportFragmentManager.backStackEntryCount == 0)
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


        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.addButton.visibility = View.VISIBLE
                binding.titleText.text = "Feeds"
            }
        }

        binding.addButton.setOnClickListener {
            supportFragmentManager.commit {
                replace<FeedNameFragment>(R.id.feedsFragment)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
            binding.titleText.text = "Create Feed"
            binding.addButton.visibility = View.INVISIBLE
        }

    } // end of onCreate

    override fun onBackPressed() {
        val sz = supportFragmentManager.fragments.size
        val fragment: Fragment? = supportFragmentManager.fragments[sz - 1]
        if (fragment !is OnBackPressed || !fragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (DatabaseClient.INSTANCE != null) {
            if (DatabaseClient.INSTANCE?.isOpen == true) {
                DatabaseClient.INSTANCE?.close()
            }
            DatabaseClient.INSTANCE = null
        }
    }
}