package com.philstar.webcurator

import android.webkit.JavascriptInterface
import com.philstar.webcurator.database.AppDatabase
import com.philstar.webcurator.database.Html

class WebJsInterfaceProvider(private val db: AppDatabase) {
    @JavascriptInterface
    fun saveHTML(html: String){
        db.userDao().deleteAll()
        db.userDao().insert(Html(0,html))
    }
}
