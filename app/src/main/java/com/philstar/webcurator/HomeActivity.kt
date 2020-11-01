package com.philstar.webcurator

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.philstar.webcurator.database.AppDatabase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {


    companion object {
        // private const val TAG = "HomeActivity"
        private var LOAD = true
    }


    private lateinit var db: AppDatabase
    private lateinit var homeViewModel: HomeActivityViewModel


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = AppDatabase.getInstance(this)

        // LiveData begins -------------------------------------------------------


        val factory = ViewModelProvider.AndroidViewModelFactory(application)

        homeViewModel = ViewModelProvider(this, factory).get(HomeActivityViewModel::class.java)

        homeViewModel.getList().observe(this, {
            if (it.isNotEmpty()) {
                outputView.text = it.first().htmlText
            }
        })
        // LiveData ends --------------------------------------------------

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebJsInterfaceProvider(db), "WebInterface")

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (LOAD) view?.loadUrl(
                        "javascript:window.WebInterface.saveHTML" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
                )
                LOAD = !LOAD

            } // onPageFinished

        } // WebViewClient

        button.setOnClickListener {
            webView.loadUrl(urlText.text.toString().trim())
        } // setOnClickListener


    } // onCreate
}









