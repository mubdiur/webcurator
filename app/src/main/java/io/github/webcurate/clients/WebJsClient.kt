package io.github.webcurate.clients

import android.webkit.JavascriptInterface
import io.github.webcurate.databases.DataProcessor
import io.github.webcurate.interfaces.OnPageFinish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebJsClient(private val callback: OnPageFinish) {
    @JavascriptInterface
    fun saveHtml(html: String) {
        CoroutineScope(Dispatchers.IO).launch {
            DataProcessor.html = html
            callback.onPageFinished()
        }
    }
}
