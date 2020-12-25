package com.mubdiur.webcurator.clients

import android.webkit.JavascriptInterface
import com.mubdiur.webcurator.interfaces.OnPageFinish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebJsClient(private val db: DatabaseClient, private val callback: OnPageFinish) {
    @JavascriptInterface
    fun saveHtml(html: String){
        CoroutineScope(Dispatchers.IO).launch {
            db.setValue("html", html)
            callback.onPageFinished()
        }
    }
}
