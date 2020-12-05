package com.mubdiur.webcurator.client

import android.webkit.JavascriptInterface

class WebJsClient(private val db: DatabaseClient) {
    @JavascriptInterface
    fun saveHtml(html: String){
        db.setValue("html", html)
    }
}
