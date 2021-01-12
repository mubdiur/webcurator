package com.mubdiur.webcurator.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mubdiur.webcurator.R

class HomeFragment : Fragment(R.layout.fragment_home) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}