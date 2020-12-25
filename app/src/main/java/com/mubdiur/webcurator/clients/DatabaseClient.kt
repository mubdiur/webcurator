package com.mubdiur.webcurator.clients

import android.content.Context
import com.mubdiur.webcurator.databases.MainDatabase
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

}