package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import io.github.webcurate.R
import io.github.webcurate.clients.MainWebViewClient
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.FragmentBrowserBinding
import io.github.webcurate.interfaces.OnPageFinish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder


@SuppressLint("ClickableViewAccessibility")
class BrowserFragment : Fragment(R.layout.fragment_browser) {


    companion object {
        var nullBinding: FragmentBrowserBinding? = null
    }

    private var _goNext: Boolean? = null
    private var _url: String? = null

    private val binding get() = nullBinding!!


    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        nullBinding = FragmentBrowserBinding.bind(view)


        binding.webFeedView.settings.javaScriptEnabled = true
        binding.webFeedView.webViewClient = MainWebViewClient(binding.urlTextFeedWeb, object : OnPageFinish {
            override fun onPageFinished() {
                binding.urlProgress.visibility = View.INVISIBLE
            }
        })
        binding.webFeedView.clearCache(true)

        binding.webFeedView.loadUrl("https://google.com")

        binding.urlTextFeedWeb.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                v.clearFocus()
                hideKeyboard()
                var url = binding.urlTextFeedWeb.text.toString().trim()
                if (Patterns.WEB_URL.matcher(url).matches()) {
                    if (!url.startsWith("http")) {
                        url = "http://$url"
                    }
                    binding.webFeedView.loadUrl(url)
                } else {
                    val searchTerm = URLEncoder.encode(url, "utf-8")
                    val searchUrl = "https://google.com/search?q=$searchTerm"
                    binding.webFeedView.loadUrl(searchUrl)
                }
                binding.urlProgress.visibility = View.VISIBLE
            }
            false
        }

        binding.goBtn.setOnClickListener {
            binding.urlTextFeedWeb.clearFocus()
            hideKeyboard()
            binding.urlProgress.visibility = View.VISIBLE
            var url = binding.urlTextFeedWeb.text.toString().trim()
            if (Patterns.WEB_URL.matcher(url).matches()) {
                if (!url.startsWith("http")) {
                    url = "http://$url"
                }
                binding.webFeedView.loadUrl(url)
            } else {
                val searchTerm = URLEncoder.encode(url, "utf-8")
                val searchUrl = "https://google.com/search?q=$searchTerm"
                binding.webFeedView.loadUrl(searchUrl)
            }
        }


        NetEvents.browserEvents.observe(requireActivity(), {
            if (it == NetEvents.LOAD_URL) {
                CoroutineScope(Dispatchers.Main).launch {
                    DataProcessor.backToFeed = true
                    NetEvents.browserEvents.value = NetEvents.DEFAULT
                    binding.urlProgress.visibility = View.VISIBLE
                    binding.webFeedView.loadUrl(DataProcessor.contentURL)
                }
            }
        })
    } //  end of onViewCreated


    private fun hideKeyboard() {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.urlTextFeedWeb.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nullBinding = null
        _goNext = null
        _url = null
    }


}