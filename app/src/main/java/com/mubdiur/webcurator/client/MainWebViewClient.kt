package com.mubdiur.webcurator.client

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.TextView
import com.mubdiur.webcurator.activity.HomeActivity

class MainWebViewClient(private val urlEdit: EditText): WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (HomeActivity.LOAD) {
            view?.loadUrl(
                "javascript:window.WebJsClient.saveHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
            )
        }
        HomeActivity.LOAD = !HomeActivity.LOAD
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        urlEdit.setText(url.toString(), TextView.BufferType.EDITABLE)
    }
}