package com.mubdiur.webcurator.activities

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.adapters.PagerAdapter
import com.mubdiur.webcurator.clients.CustomTitle
import com.mubdiur.webcurator.databases.DataProcessor
import com.mubdiur.webcurator.databases.DatabaseClient
import com.mubdiur.webcurator.databinding.ActivityMainBinding
import com.mubdiur.webcurator.fragments.FeedNameFragment
import com.mubdiur.webcurator.interfaces.OnBackPressed


class MainActivity : AppCompatActivity() {

    companion object {
        var nullBinding: ActivityMainBinding? = null
    }

    val binding get() = nullBinding!!
    private val pages = arrayOf("Home", "Feeds", "Browser", "Manage")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nullBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        CustomTitle.active
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
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_home)
                1 -> tab.setIcon(R.drawable.ic_feed)
                2 -> tab.setIcon(R.drawable.ic_browser)
                else -> tab.setIcon(R.drawable.ic_manage)
            }
            tab.text = ""
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
                DataProcessor.currentRootPosition = position
                when (position) {
                    1 -> {
                        binding.titleText.visibility = View.VISIBLE
                        when {
                            supportFragmentManager.backStackEntryCount == 0 -> binding.titleText.text =
                                pages[position]
                            CustomTitle.active -> binding.titleText.text = CustomTitle.currentTitle
                            else -> binding.titleText.text = pages[position]
                        }

                        if (supportFragmentManager.backStackEntryCount == 0)
                            binding.menuButton.visibility = View.VISIBLE

                    }
                    else -> {
                        binding.menuButton.visibility = View.VISIBLE
                        // Show stuff for Home
                        binding.titleText.visibility = View.VISIBLE
                        binding.titleText.text = pages[position]
                    }
                } // end of when
            } // end of on page selected
        }) // end of registerOnPageChangeCallback


        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.menuButton.visibility = View.VISIBLE
                binding.titleText.text = "Feeds"
            }
        }

        binding.menuButton.setOnClickListener {
            CustomTitle.setTitle("Create Feed - Basic Info")
            supportFragmentManager.commit {
                replace<FeedNameFragment>(R.id.feedsFragment)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
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