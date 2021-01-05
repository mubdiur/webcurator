package com.mubdiur.webcurator.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.mubdiur.webcurator.interfaces.OnBackPressed
import com.mubdiur.webcurator.interfaces.OnPageFinish
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.clients.MainWebViewClient
import com.mubdiur.webcurator.clients.WebJsClient
import com.mubdiur.webcurator.databases.DatabaseClient
import com.mubdiur.webcurator.databases.models.Value
import com.mubdiur.webcurator.databinding.FragmentPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("ClickableViewAccessibility")
class PageFragment : Fragment(R.layout.fragment_page), OnBackPressed, OnPageFinish {


    private var _binding: FragmentPageBinding? = null
    private var _db: DatabaseClient? = null

    private var _goNext: Boolean? = null
    private var _url: String? = null

    private val binding get() = _binding!!
    private val db get() = _db!!


    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        _binding = FragmentPageBinding.bind(view)
        _db = DatabaseClient.getInstance(requireContext())
        _goNext = false

        binding.webFeedView.settings.javaScriptEnabled = true
        binding.webFeedView.addJavascriptInterface(WebJsClient(db, this), "WebJsClient")
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


        binding.pageNext.setOnClickListener {
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
        if (_goNext==true) {
            _goNext = false
            _url = binding.urlTextFeedWeb.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    db.valueDao().insertValue(Value("url", _url!!))
                } catch (e: Exception) {}
                withContext(Dispatchers.Main) {
                    requireActivity().supportFragmentManager.commit {
                        replace<SelectionFragment>(R.id.pageFragment)
                        setReorderingAllowed(true)
                        addToBackStack(null)
                    }
                }
            }
        }
    }


    private fun hideKeyboard() {
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(binding.urlTextFeedWeb.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _db = null
        _goNext = null
        _url = null
    }

}