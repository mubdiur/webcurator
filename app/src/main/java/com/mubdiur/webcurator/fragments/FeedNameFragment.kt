package com.mubdiur.webcurator.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.clients.DatabaseClient
import com.mubdiur.webcurator.databinding.FragmentFeedNameBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class FeedNameFragment : Fragment(R.layout.fragment_feed_name) {
    private var _binding: FragmentFeedNameBinding? = null
    private var _db: DatabaseClient? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated (view, savedInstanceState)
        view.setOnTouchListener { _, _ ->  true }

        val binding = FragmentFeedNameBinding.bind(view)
        _binding = binding
        val db = DatabaseClient.getClient(requireContext())
        _db = db
        binding.feedNameNext.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    db.setValue(
                        "title",
                        binding.feedTitleEdit.text.toString()
                    )
                    db.setValue(
                        "Description",
                        binding.descriptionEdit.text.toString()
                    )
                } catch (e: Exception){}
            }
            requireActivity().supportFragmentManager.commit {
                replace<PageFragment>(R.id.feedNameFragment,  tag = "pageFragment")
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        _db = null
        super.onDestroy()
    }
}