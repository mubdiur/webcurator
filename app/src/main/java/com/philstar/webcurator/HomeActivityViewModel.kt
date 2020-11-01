package com.philstar.webcurator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.philstar.webcurator.database.AppDatabase
import com.philstar.webcurator.database.Html
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivityViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    fun getList(): LiveData<List<Html>> {
        return db.userDao().getAll()
    }

}