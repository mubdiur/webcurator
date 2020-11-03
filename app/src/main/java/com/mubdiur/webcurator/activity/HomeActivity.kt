package com.mubdiur.webcurator.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.mubdiur.webcurator.R
import com.mubdiur.webcurator.client.DatabaseClient
import com.mubdiur.webcurator.client.MainWebViewClient
import com.mubdiur.webcurator.client.WebJsClient
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {


    companion object {
        // private const val TAG = "HomeActivity"
        var LOAD = true
    }

    private lateinit var dbClient: DatabaseClient


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.hide()

        dbClient = DatabaseClient.getClient(this)

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebJsClient(dbClient), "WebJsClient")
        webView.webViewClient = MainWebViewClient(urlText)



        urlText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                closeKeyBoard()
                v.clearFocus()
                webView.loadUrl(v.text.toString())
                true
            } else {
                false
            }
        }

        homeNextButton.setOnClickListener {
        } // button.setOnClickListener


    } // onCreate



    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}








