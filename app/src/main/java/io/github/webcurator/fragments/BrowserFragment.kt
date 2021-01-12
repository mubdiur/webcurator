package io.github.webcurator.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.github.webcurator.R
import io.github.webcurator.clients.MainWebViewClient
import io.github.webcurator.clients.WebJsClient
import io.github.webcurator.databinding.FragmentBrowserBinding
import io.github.webcurator.interfaces.OnBackPressed
import io.github.webcurator.interfaces.OnPageFinish


@SuppressLint("ClickableViewAccessibility")
class BrowserFragment : Fragment(R.layout.fragment_browser), OnBackPressed {

    companion object {
        fun addPageToFeed(fragmentManager: FragmentManager) {
            TODO()
        }
    }

    private var _binding: FragmentBrowserBinding? = null

    private var _goNext: Boolean? = null
    private var _url: String? = null

    private val binding get() = _binding!!


    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        _binding = FragmentBrowserBinding.bind(view)


        binding.webFeedView.settings.javaScriptEnabled = true
        binding.webFeedView.addJavascriptInterface(WebJsClient(object : OnPageFinish {
            override fun onPageFinished() {
                // Do nothing
            }
        }), "WebJsClient")
        binding.webFeedView.webViewClient = MainWebViewClient(binding.urlTextFeedWeb)
        binding.webFeedView.clearCache(true)


        binding.urlTextFeedWeb.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                v.clearFocus()
                hideKeyboard()
                binding.webFeedView.loadUrl(binding.urlTextFeedWeb.text.toString().trim())
            }
            false
        }

        binding.goBtn.setOnClickListener {
            binding.urlTextFeedWeb.clearFocus()
            hideKeyboard()
            binding.webFeedView.loadUrl(binding.urlTextFeedWeb.text.toString().trim())
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


    private fun hideKeyboard() {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.urlTextFeedWeb.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _goNext = null
        _url = null
    }


}