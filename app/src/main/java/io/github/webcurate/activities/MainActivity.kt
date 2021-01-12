package io.github.webcurate.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import io.github.webcurate.R
import io.github.webcurate.adapters.PagerAdapter
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.databases.DataProcessor
import io.github.webcurate.databases.DatabaseClient
import io.github.webcurate.databinding.ActivityMainBinding
import io.github.webcurate.fragments.BrowserFragment
import io.github.webcurate.fragments.FeedContentFragment
import io.github.webcurate.fragments.FeedsFragment
import io.github.webcurate.interfaces.OnBackPressed
import io.github.webcurate.options.OptionMenu


class MainActivity : AppCompatActivity() {

    companion object {
        var nullBinding: ActivityMainBinding? = null
    }

    val binding get() = nullBinding!!
    private val pages = arrayOf("Home", "Feeds", "Browser", "Manage")


    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        nullBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        /**
         *  Hides the default actionbar at top
         * */
        supportActionBar?.hide()


        /**
         * ViewPager Part
         * */
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

        // Starts the database client with application context
        DatabaseClient.getInstance(this.applicationContext)


        /**
         * Change top bar and option context according to pages
         * */
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                DataProcessor.currentRootPosition = position
                when (position) {
                    1 -> {
                        when {
                            supportFragmentManager.backStackEntryCount == 0 -> {
                                binding.titleText.text = pages[position]
                                OptionMenu.contextType = OptionMenu.CONTEXT_FEED
                            }
                            CustomTitle.active -> {
                                binding.titleText.text = CustomTitle.currentTitle
                                if (OptionMenu.feedItemVisible) OptionMenu.contextType =
                                    OptionMenu.CONTEXT_FEED_ITEM
                                else OptionMenu.contextType = OptionMenu.CONTEXT_DEFAULT
                            }
                        }
                    }
                    2 -> { // Browser
                        OptionMenu.contextType = OptionMenu.CONTEXT_BROWSER
                        binding.titleText.text = pages[position]
                    }
                    else -> {
                        OptionMenu.contextType = OptionMenu.CONTEXT_DEFAULT
                        binding.titleText.text = pages[position]
                    }
                } // end of when
            } // end of on page selected
        }) // end of registerOnPageChangeCallback
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.menuButton.visibility = View.VISIBLE
                binding.titleText.text = "Feeds"
                OptionMenu.contextType = OptionMenu.CONTEXT_FEED
            }
        }


        // add onclick to optionMenu to mask the clicks
        binding.optionMenu.setOnTouchListener { _, _ -> true }


        // Handle Menu button
        binding.menuButton.setOnClickListener {

            // first hide and show buttons based on context
            if (!OptionMenu.isVisible) {
                // set all contextual buttons to gone
                binding.addFeed.visibility = View.GONE
                binding.addToFeed.visibility = View.GONE
                binding.addSite.visibility = View.GONE
                binding.editFeed.visibility = View.GONE
                binding.notificationToggle.visibility = View.GONE
                binding.manageSites.visibility = View.GONE
                binding.deleteFeed.visibility = View.GONE


                when (OptionMenu.contextType) {
                    OptionMenu.CONTEXT_FEED_ITEM -> {
                        binding.addSite.visibility = View.VISIBLE
                        binding.editFeed.visibility = View.VISIBLE
                        if (OptionMenu.notify) {
                            binding.notificationToggle.text = "Turn off Notification"
                        } else {
                            binding.notificationToggle.text = "Turn on Notification"
                        }
                        binding.notificationToggle.visibility = View.VISIBLE
                        binding.manageSites.visibility = View.VISIBLE
                        binding.deleteFeed.visibility = View.VISIBLE
                    }
                    OptionMenu.CONTEXT_FEED -> {
                        binding.addFeed.visibility = View.VISIBLE
                    }
                    OptionMenu.CONTEXT_BROWSER -> {
                        binding.addToFeed.visibility = View.VISIBLE
                    }
                }
                showOptions()

            } else {
                hideOptions()
            }
        }

        // each buttons in menu button
        binding.addFeed.setOnClickListener {
            hideOptions()
            if (OptionMenu.contextType != OptionMenu.CONTEXT_DEFAULT) {
                FeedsFragment.addFeed(supportFragmentManager)
            }
        }
        binding.addToFeed.setOnClickListener {
            hideOptions()
            if (OptionMenu.contextType != OptionMenu.CONTEXT_DEFAULT) {
                BrowserFragment.addPageToFeed(supportFragmentManager)
            }
        }
        binding.addSite.setOnClickListener {
            hideOptions()
            if (OptionMenu.contextType != OptionMenu.CONTEXT_DEFAULT) {
                FeedContentFragment.addSite(supportFragmentManager)
            }
        }
        binding.editFeed.setOnClickListener {
            hideOptions()
            if (OptionMenu.contextType != OptionMenu.CONTEXT_DEFAULT) {
                FeedContentFragment.editFeed(supportFragmentManager)
            }
        }
        binding.manageSites.setOnClickListener {
            hideOptions()
            if (OptionMenu.contextType != OptionMenu.CONTEXT_DEFAULT) {
                FeedContentFragment.manageSites(supportFragmentManager)
            }
        }
        binding.deleteFeed.setOnClickListener {
            hideOptions()
            if (OptionMenu.contextType != OptionMenu.CONTEXT_DEFAULT) {
                FeedContentFragment.deleteFeed()
            }
        }
        binding.notificationToggle.setOnClickListener {
            hideOptions()
            if (OptionMenu.contextType != OptionMenu.CONTEXT_DEFAULT) {
                FeedContentFragment.toggleNotification()
            }
        }


        binding.helpButton.setOnClickListener {
            hideOptions()
        }
        binding.aboutButton.setOnClickListener {
            hideOptions()
        }
        binding.logoutButton.setOnClickListener {
            hideOptions()
        }
        binding.cancelButton.setOnClickListener {
            hideOptions()
        }
    } // end of onCreate

    private fun showOptions() {
        binding.optionMenu.visibility = View.VISIBLE
        OptionMenu.isVisible = true
        binding.tabBar.visibility = View.GONE
    }

    private fun hideOptions() {
        binding.optionMenu.visibility = View.GONE
        OptionMenu.isVisible = false
        binding.tabBar.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        val fragment: Fragment? = supportFragmentManager.fragments.last()
        if (OptionMenu.isVisible) hideOptions()
        else if (fragment !is OnBackPressed || !fragment.onBackPressed()) {
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