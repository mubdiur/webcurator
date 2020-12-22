package com.mubdiur.webcurator.fragment

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.mubdiur.webcurator.OnBackPressed
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.client.DatabaseClient
import com.mubdiur.webcurator.client.MainWebViewClient
import com.mubdiur.webcurator.client.WebJsClient
import com.mubdiur.webcurator.databinding.FragmentPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


class PageFragment : OnBackPressed, Fragment(R.layout.fragment_page) {
    private var _binding: FragmentPageBinding? = null
    private var _db: DatabaseClient? = null

    companion object {
        @JvmStatic
        var activated = true
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val binding = FragmentPageBinding.bind(view)
        _binding = binding
        val db = DatabaseClient(requireContext())
        _db = db

        binding.webFeedView.settings.javaScriptEnabled = true
        binding.webFeedView.addJavascriptInterface(WebJsClient(db), "WebJsClient")
        binding.webFeedView.webViewClient = MainWebViewClient(binding.urlTextFeedWeb)


        binding.urlTextFeedWeb.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val vw = requireActivity().currentFocus
                if (vw != null) {
                    val imm =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                v.clearFocus()
                binding.webFeedView.loadUrl(v.text.toString())
                true
            } else {
                false
            }
        }



        binding.pageNext.setOnClickListener {
            val url = binding.urlTextFeedWeb.text.toString()
            binding.webFeedView.loadUrl(url)

            // TODO run the following block only after loadUrl has finished its job
            requireActivity().supportFragmentManager.commit {
                replace<SelectionFragment>(R.id.pageFragment)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    } //  end of onViewCreated

    override fun onBackPressed(): Boolean {
        return if (activated && _binding?.webFeedView?.canGoBack() == true) {
            _binding?.webFeedView?.goBack()
            true
        } else {
            false
        }
    }


    override fun onDestroy() {
        _binding = null
        _db = null
        super.onDestroy()
    }

}