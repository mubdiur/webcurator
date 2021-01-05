package com.mubdiur.webcurator.clients

import android.webkit.JavascriptInterface
import com.mubdiur.webcurator.databases.DatabaseClient
import com.mubdiur.webcurator.databases.models.Value
import com.mubdiur.webcurator.interfaces.OnPageFinish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebJsClient(private val db: DatabaseClient, private val callback: OnPageFinish) {
    @JavascriptInterface
    fun saveHtml(html: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.valueDao().insertValue(Value("html", html))
                callback.onPageFinished()
            } catch (e: Exception){}
        }
    }
}
