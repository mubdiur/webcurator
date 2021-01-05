package com.mubdiur.webcurator.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mubdiur.webcurator.databases.DatabaseClient
import com.mubdiur.webcurator.databases.models.Feed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

//class FeedsViewModel(application: Application) : AndroidViewModel(
//    application
//) {
//    private var feedList = flowOf<List<Feed>>()
//
//    init {
//        viewModelScope.launch {
//            DatabaseClient.getInstance(application).feedDao().getAllFeeds().collect {
//                feedList = it
//            }
//        }
//    }
//
//    fun feedList() = feedList
//}