package io.github.webcurate.clients

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.TextView
import io.github.webcurate.activities.MainActivity
import io.github.webcurate.interfaces.OnPageFinish

class MainWebViewClient(private val urlEdit: EditText, private val callback: OnPageFinish) :
    WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        MainActivity.stopLoadingAnimation()
        callback.onPageFinished()
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        MainActivity.startLoadingAnimation()
        urlEdit.setText(url.toString(), TextView.BufferType.EDITABLE)
    }
}