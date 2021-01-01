package com.mubdiur.webcurator.databases.repositories

import com.mubdiur.webcurator.databases.MainDatabase
import com.mubdiur.webcurator.databases.models.Site

class SiteRepository (db: MainDatabase) {

    private val dao = db.siteDao()

    // Make singleton
    companion object {

        private var INSTANCE: SiteRepository? = null

        fun getInstance(db: MainDatabase): SiteRepository {
            if (INSTANCE == null) {
                INSTANCE = SiteRepository(db)
            }
            return INSTANCE as SiteRepository
        }
    }

    fun setSite(url: String, queries: List<String>) {
        dao.insertSite(Site(url, queries))
    }

    fun getAllSites(): List<Site> {
        return dao.getAllSites()
    }

    fun deleteAllSites(): Int {
        return dao.deleteSiteAll()
    }
}