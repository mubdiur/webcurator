package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.github.webcurate.R
import io.github.webcurate.databinding.FragmentFeedEditBinding

class FeedEditFragment : Fragment(R.layout.fragment_feed_edit) {

    private var binding: FragmentFeedEditBinding? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}