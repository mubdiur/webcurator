package com.mubdiur.webcurator.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mubdiur.webcurator.fragments.BrowserFragment
import com.mubdiur.webcurator.fragments.FeedsFragment
import com.mubdiur.webcurator.fragments.HomeFragment

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> {
                return HomeFragment()
            }
            1 -> {
                return FeedsFragment()
            }
        }
        return BrowserFragment()
    }
}