package com.mubdiur.webcurator.client

import android.content.Context
import com.mubdiur.webcurator.database.MainDatabase
import com.mubdiur.webcurator.database.model.Html
import com.mubdiur.webcurator.database.repository.HtmlRepository

class DatabaseClient(context: Context) {

    private val db = MainDatabase.getInstance(context)

    // Make singleton
    companion object {
        private var client: DatabaseClient? = null

        fun getClient(context: Context): DatabaseClient {
            if (client == null) {
                client = DatabaseClient(context)
            }
            return client as DatabaseClient
        }
    }

    // Html Table ----------------------------------------------------------

    fun insertHtml(htmlText: String) {
        HtmlRepository.getInstance(db).insertHtml(Html(0, htmlText))
    }

    fun getHtml(): Html {
        return HtmlRepository.getInstance(db).getHtml()
    }

    fun deleteHtml() {
        HtmlRepository.getInstance(db).deleteHtml()
    }
    // end of Html Table ---------------------------------------------------
}