package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import io.github.webcurate.R
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.databases.DatabaseClient
import io.github.webcurate.databases.models.Value
import io.github.webcurate.databinding.FragmentFeedNameBinding
import io.github.webcurate.options.OptionMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedNameFragment : Fragment(R.layout.fragment_feed_name) {

    private var _binding: FragmentFeedNameBinding? = null
    private var _db: DatabaseClient? = null
    private val binding get() = _binding!!
    private val db get() = _db!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
        _binding = FragmentFeedNameBinding.bind(view)
        _db = DatabaseClient.getInstance(requireContext())

        // set option menu context to default coming from feed
        OptionMenu.contextType = OptionMenu.CONTEXT_DEFAULT


        binding.feedNameNext.setOnClickListener {
            if (binding.descriptionEdit.text.toString().isEmpty()) {
                binding.descriptionEdit.requestFocus()
                binding.descriptionEdit.error = "This field cannot be blank"
            }
            if (binding.feedTitleEdit.text.toString().isEmpty()) {
                binding.feedTitleEdit.requestFocus()
                binding.feedTitleEdit.error = "This field cannot be blank"
            }
            if (
                binding.feedTitleEdit.text.toString().isNotEmpty()
                && binding.descriptionEdit.text.toString().isNotEmpty()
            ) {


                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        db.valueDao().insertValue(
                            Value(
                                "title",
                                binding.feedTitleEdit.text.toString()
                            )
                        )
                        db.valueDao().insertValue(
                            Value(
                                "description",
                                binding.descriptionEdit.text.toString()
                            )
                        )
                    } catch (e: Exception) {
                    }
                }
                requireActivity().supportFragmentManager.commit {
                    replace<PageFragment>(R.id.feedNameFragment, tag = "pageFragment")
                    setReorderingAllowed(true)
                    addToBackStack(null)
                }
            } // if
        } // onClickListener
    } // onViewCreated

    override fun onDestroyView() {
        super.onDestroyView()
        CustomTitle.resetTitle()
        // set option menu context to feed going back to feed
        OptionMenu.contextType = OptionMenu.CONTEXT_FEED
        _binding = null
        _db = null
    }
}