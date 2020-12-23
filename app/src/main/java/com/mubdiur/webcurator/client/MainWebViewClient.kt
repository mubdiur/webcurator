package com.mubdiur.webcurator.client

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.TextView

class MainWebViewClient(private val urlEdit: EditText) : WebViewClient() {


    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.loadUrl(
            "javascript:window.WebJsClient.saveHtml" +
                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
        )
        Log.d(TAG, "onPageFinished: called.......from web view")
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        urlEdit.setText(url.toString(), TextView.BufferType.EDITABLE)
    }
}