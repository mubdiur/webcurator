package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import io.github.webcurate.R
import io.github.webcurate.clients.CustomTitle
import io.github.webcurate.clients.MainWebViewClient
import io.github.webcurate.clients.WebJsClient
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.databinding.FragmentPageBinding
import io.github.webcurate.interfaces.OnBackPressed
import io.github.webcurate.interfaces.OnPageFinish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("ClickableViewAccessibility")
class PageFragment : Fragment(R.layout.fragment_page), OnBackPressed, OnPageFinish {


    private var _binding: FragmentPageBinding? = null

    private var _goNext: Boolean? = null

    private val binding get() = _binding!!


    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }



        _binding = FragmentPageBinding.bind(view)
        _goNext = false



        binding.webFeedView.settings.javaScriptEnabled = true
        binding.webFeedView.addJavascriptInterface(WebJsClient(this), "WebJsClient")
        binding.webFeedView.webViewClient = MainWebViewClient(binding.urlTextFeedWeb)
        binding.webFeedView.clearCache(true)

        if (DataProcessor.siteModifyMode) {
            CustomTitle.setTitle("Modify Site - Page")
            binding.webFeedView.loadUrl(DataProcessor.currentSite!!.url)
        } else if (DataProcessor.siteAddMode) {
            CustomTitle.setTitle("Add Site - Page")
        }
        else {
            CustomTitle.setTitle("Create Feed - Page")
        }

        binding.urlTextFeedWeb.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                v.clearFocus()
                hideKeyboard()
                binding.urlProgress.visibility = View.VISIBLE
                binding.webFeedView.loadUrl(binding.urlTextFeedWeb.text.toString().trim())
            }
            false
        }

        binding.goBtn.setOnClickListener {
            binding.urlTextFeedWeb.clearFocus()
            hideKeyboard()
            binding.urlProgress.visibility = View.VISIBLE
            binding.webFeedView.loadUrl(binding.urlTextFeedWeb.text.toString().trim())
        }

        binding.pageNext.setOnClickListener {
            hideKeyboard()
            _goNext = true
            binding.webFeedView.reload()
        }
    } //  end of onViewCreated

    override fun onBackPressed(): Boolean {
        return if (binding.webFeedView.canGoBack()) {
            binding.webFeedView.goBack()
            true
        } else {
            false
        }
    }


    override fun onPageFinished() {
        CoroutineScope(Dispatchers.Main).launch { binding.urlProgress.visibility = View.INVISIBLE }
        if (_goNext == true) {
            _goNext = false
            DataProcessor.feedCreationUrl = binding.urlTextFeedWeb.text.toString()

            requireActivity().supportFragmentManager.commit {
                replace<SelectionFragment>(R.id.pageFragment)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }


    private fun hideKeyboard() {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.urlTextFeedWeb.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        CustomTitle.pop()
        _binding = null
        _goNext = null
        DataProcessor.siteModifyMode = false
        DataProcessor.siteAddMode = false
    }

}