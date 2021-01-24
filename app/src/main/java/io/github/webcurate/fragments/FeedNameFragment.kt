package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import io.github.webcurate.R
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.databinding.FragmentFeedNameBinding
import io.github.webcurate.options.OptionMenu

class FeedNameFragment : Fragment(R.layout.fragment_feed_name) {

    private var _binding: FragmentFeedNameBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }
        _binding = FragmentFeedNameBinding.bind(view)

        // set option menu context to default coming from feed
        OptionMenu.contextType = OptionMenu.CONTEXT_DEFAULT


        binding.feedNameNext.setOnClickListener {
            hideKeyboard()
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
                DataProcessor.feedCreationTitle = binding.feedTitleEdit.text.toString()
                DataProcessor.feedCreationDescription = binding.descriptionEdit.text.toString()

                requireActivity().supportFragmentManager.commit {
                    replace<PageFragment>(R.id.feedNameFragment, tag = "pageFragment")
                    setReorderingAllowed(true)
                    addToBackStack(null)
                }
            } // if
        } // onClickListener
    } // onViewCreated

    private fun hideKeyboard() {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.descriptionEdit.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        CustomTitle.resetTitle()
        // set option menu context to feed going back to feed
        OptionMenu.contextType = OptionMenu.CONTEXT_FEED
        _binding = null
    }
}