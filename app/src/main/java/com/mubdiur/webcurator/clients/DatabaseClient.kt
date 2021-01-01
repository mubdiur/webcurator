package com.mubdiur.webcurator.clients

import android.content.Context
import com.mubdiur.webcurator.databases.MainDatabase
import com.mubdiur.webcurator.databases.models.Site
import com.mubdiur.webcurator.databases.repositories.SiteRepository
import com.mubdiur.webcurator.databases.repositories.ValueRepository

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

    // Value Table ----------------------------------------------------------

    fun setValue(key: String, value: String) {
        ValueRepository.getInstance(db).setValue(key, value)

    }

    fun getValue(key: String): String {
        return ValueRepository.getInstance(db).getValue(key)
    }

    fun deleteValue(key: String) {
        ValueRepository.getInstance(db).deleteValue(key)
    }

    fun deleteValueAll() {
        ValueRepository.getInstance(db).deleteValueAll()
    }
    // end of Value Table ---------------------------------------------------

    // Site Table ----------------------------------------------------------

    fun setSite(url: String, queries: List<String>) {
        SiteRepository.getInstance(db).setSite(url, queries)
    }

    fun getAllSites(): List<Site> {
        return SiteRepository.getInstance(db).getAllSites()
    }

    fun deleteAllSites() {
        SiteRepository.getInstance(db).deleteAllSites()
    }
    // end of Site Table ---------------------------------------------------

}