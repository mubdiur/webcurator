package com.mubdiur.webcurator.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.client.DatabaseClient
import com.mubdiur.webcurator.databinding.FragmentFeedNameBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FeedNameFragment : Fragment(R.layout.fragment_feed_name) {
    private var _binding: FragmentFeedNameBinding? = null
    private var _db: DatabaseClient? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated (view, savedInstanceState)
        val binding = FragmentFeedNameBinding.bind(view)
        _binding = binding
        val db = DatabaseClient(requireContext())
        _db = db
        binding.feedNameNext.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.setValue(
                    "title",
                    binding.feedTitleEdit.text.toString()
                )
                db.setValue(
                    "Description",
                    binding.descriptionEdit.text.toString()
                )
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