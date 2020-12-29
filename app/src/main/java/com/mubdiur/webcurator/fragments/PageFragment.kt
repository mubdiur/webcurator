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
import com.mubdiur.webcurator.clients.DatabaseClient
import com.mubdiur.webcurator.clients.MainWebViewClient
import com.mubdiur.webcurator.clients.WebJsClient
import com.mubdiur.webcurator.databinding.FragmentPageBinding


@SuppressLint("ClickableViewAccessibility")
class PageFragment : Fragment(R.layout.fragment_page), OnBackPressed, OnPageFinish {
    private var _binding: FragmentPageBinding? = null
    private var _db: DatabaseClient? = null
    private var goNext = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        val binding = FragmentPageBinding.bind(view)
        _binding = binding
        val db = DatabaseClient(requireContext())
        _db = db

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
            goNext = true
            binding.webFeedView.reload()
        }
    } //  end of onViewCreated

    override fun onBackPressed(): Boolean {
        return if (_binding?.webFeedView?.canGoBack() == true) {
            _binding?.webFeedView?.goBack()
            true
        } else {
            false
        }
    }


    override fun onPageFinished() {
        if (goNext) {
            goNext = false
            requireActivity().supportFragmentManager.commit {
                replace<SelectionFragment>(R.id.pageFragment)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }


    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(_binding?.urlTextFeedWeb?.windowToken, 0)
    }

    override fun onDestroy() {
        _binding = null
        _db = null
        super.onDestroy()
    }

}