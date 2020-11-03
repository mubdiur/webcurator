package com.mubdiur.webcurator.database.repository

import com.mubdiur.webcurator.database.MainDatabase
import com.mubdiur.webcurator.database.model.Html

class HtmlRepository(db: MainDatabase) {

    private val dao = db.userDao()

    // Make singleton
    companion object {
        private var INSTANCE: HtmlRepository? = null

        fun getInstance(db: MainDatabase): HtmlRepository {
            if (INSTANCE == null) {
                INSTANCE = HtmlRepository(db)
            }
            return INSTANCE as HtmlRepository
        }
    }

    fun insertHtml(html: Html) {
        dao.deleteHtml()
        dao.insertHtml(html)
    }


    fun getHtml(): Html {
        return dao.getHtml()
    }
    fun deleteHtml() {
        dao.deleteHtml()
    }
}