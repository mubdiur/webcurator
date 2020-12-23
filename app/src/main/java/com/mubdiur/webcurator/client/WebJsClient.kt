package com.mubdiur.webcurator.client

import android.webkit.JavascriptInterface
import com.mubdiur.webcurator.OnPageFinish

class WebJsClient(private val db: DatabaseClient, private val callback: OnPageFinish) {
    @JavascriptInterface
    fun saveHtml(html: String){
        db.setValue("html", html)
        callback.onPageFinished()
    }
}
