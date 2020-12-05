package com.mubdiur.webcurator.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.databinding.FragmentFeedNameBinding

class FeedNameFragment : Fragment(R.layout.fragment_feed_name) {
    private var _binding: FragmentFeedNameBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentFeedNameBinding.bind(view)
        _binding = binding
        binding.feedNameNext.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace<PageFragment>(R.id.feedNameFragment)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}