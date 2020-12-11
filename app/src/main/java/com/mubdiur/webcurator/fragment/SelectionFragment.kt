package com.mubdiur.webcurator.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.databinding.FragmentSelectionBinding


class SelectionFragment : Fragment(R.layout.fragment_selection) {
    private var _binding: FragmentSelectionBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PageFragment.activated = false
        val binding = FragmentSelectionBinding.bind(view)
        _binding = binding

        binding.selectionFinish.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onDestroy() {
        _binding = null
        PageFragment.activated = true
        super.onDestroy()
    }
}