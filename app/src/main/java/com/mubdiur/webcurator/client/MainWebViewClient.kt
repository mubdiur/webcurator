package com.mubdiur.webcurator.client

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.TextView

class MainWebViewClient(private val urlEdit: EditText): WebViewClient() {
    companion object {
        // private const val TAG = "HomeActivity"
        var LOAD = true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (LOAD) {
            view?.loadUrl(
                "javascript:window.WebJsClient.saveHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
            )
        }
        LOAD = !LOAD
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        urlEdit.setText(url.toString(), TextView.BufferType.EDITABLE)
    }
}