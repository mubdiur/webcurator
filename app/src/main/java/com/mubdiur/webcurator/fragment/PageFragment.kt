package com.mubdiur.webcurator.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.databinding.FragmentPageBinding


class PageFragment : Fragment(R.layout.fragment_page){
    private var _binding: FragmentPageBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPageBinding.bind(view)
        _binding = binding

        binding.pageNext.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace<SelectionFragment>(R.id.pageFragment)
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