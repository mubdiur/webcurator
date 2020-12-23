package com.mubdiur.webcurator.activity
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.os.Bundle
//import android.view.inputmethod.EditorInfo
//import android.view.inputmethod.InputMethodManager
//import androidx.appcompat.app.AppCompatActivity
//import com.mubdiur.webcurator.client.DatabaseClient
//import com.mubdiur.webcurator.client.MainWebViewClient
//import com.mubdiur.webcurator.client.WebJsClient
//import com.mubdiur.webcurator.databinding.ActivityHomeBinding
//
//
//class HomeActivity : AppCompatActivity() {
//
//
//    companion object {
//        // private const val TAG = "HomeActivity"
//    }
//
//    private lateinit var binding: ActivityHomeBinding
//
//    private lateinit var dbClient: DatabaseClient
//
//
//    @SuppressLint("SetJavaScriptEnabled")
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        binding = ActivityHomeBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        supportActionBar?.hide()
//
//        dbClient = DatabaseClient.getClient(this)
//
//        binding.webView.settings.javaScriptEnabled = true
//        binding.webView.addJavascriptInterface(WebJsClient(dbClient), "WebJsClient")
//        binding.webView.webViewClient = MainWebViewClient(binding.urlText)
//
//
//
//        binding.urlText.setOnEditorActionListener { v, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_GO) {
//                closeKeyBoard()
//                v.clearFocus()
//                binding.webView.loadUrl(v.text.toString())
//                true
//            } else {
//                false
//            }
//        }
//
//        binding.homeNextButton.setOnClickListener {
//        } // button.setOnClickListener
//
//
//    } // onCreate
//
//
//
//    private fun closeKeyBoard() {
//        val view = this.currentFocus
//        if (view != null) {
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(view.windowToken, 0)
//        }
//    }
//}
//
//
//
//
//
//
//
//
