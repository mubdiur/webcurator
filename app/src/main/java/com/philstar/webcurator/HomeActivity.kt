package com.philstar.webcurator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.philstar.webcurator.database.AppDatabase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "HomeActivity"
        private var LOAD = true
    }


    private lateinit var db: AppDatabase


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = AppDatabase.getInstance(this)

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebJsInterfaceProvider(db), "WebInterface")

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (LOAD) {
                    view?.loadUrl(
                        "javascript:window.WebInterface.saveHTML" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
                    )
                    LOAD = !LOAD
                } else LOAD = !LOAD

            } // onPageFinished

        } // WebViewClient


        button.setOnClickListener {

            val testing = false
            if (!testing) webView.loadUrl("https://google.com")
            else CoroutineScope(Dispatchers.IO).launch {
                val list = db.userDao().getAll()
                Log.d(TAG, "onCreate: ${list.size}")
            } // CoroutineScope

        } // setOnClickListener


    } // onCreate
}









