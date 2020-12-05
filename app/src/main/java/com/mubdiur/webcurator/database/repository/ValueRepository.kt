package com.mubdiur.webcurator.database.repository

import com.mubdiur.webcurator.database.MainDatabase
import com.mubdiur.webcurator.database.model.Value

class ValueRepository (db: MainDatabase) {

    private val dao = db.valueDao()

    // Make singleton
    companion object {
        private var INSTANCE: ValueRepository? = null

        fun getInstance(db: MainDatabase): ValueRepository {
            if (INSTANCE == null) {
                INSTANCE = ValueRepository(db)
            }
            return INSTANCE as ValueRepository
        }
    }

    fun setValue(key: String, value: String) {
        dao.deleteValue(key)
        dao.insertValue(Value(0, key, value))
    }


    fun getValue(key:String): String {
        return dao.getValue(key)
    }

    fun deleteValue(key: String): Int {
        return dao.deleteValue(key)
    }

    fun deleteValueAll(): Int {
        return dao.deleteValueAll()
    }
}