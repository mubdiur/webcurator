package io.github.webcurate.networking.apis

import com.haroldadmin.cnradapter.NetworkResponse
import io.github.webcurate.data.DataProcessor
import io.github.webcurate.data.NetEvents
import io.github.webcurate.networking.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger

object Repository {
    // variables for response
    var feedList = listOf<FeedResponse>()
    var siteList = listOf<SiteResponse>()
    var contentList = listOf<ContentResponse>()
    var topic = ""
    var notification = 0

    // ------ 1. FETCH operations ---------- //
    // network 1
    suspend fun getUserFeeds() {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getUserFeeds()) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    feedList = response.body
                    NetEvents.feedEvents.postValue(NetEvents.FEEDS_READY)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // network 2
    suspend fun getSitesForFeed(feedid: BigInteger) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getSitesForFeed(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    siteList = response.body
                    NetEvents.siteEvents.postValue(NetEvents.SITES_READY)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // network 3
    suspend fun getContentsForFeed(feedid: BigInteger) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getContentsForFeed(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    contentList = response.body

                    NetEvents.contentEvents.postValue(NetEvents.CONTENTS_READY)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // network 4
    suspend fun getUpdateCount(feedid: BigInteger) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getUpdateCount(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // network 5
    suspend fun getTopic() {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getTopic()) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    topic = response.body

                    NetEvents.topicEvents.postValue(NetEvents.TOPIC_READY)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // network 6
    suspend fun getNotificationStatus(feedid: BigInteger) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getNotificationStatus(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    notification = response.body

                    NetEvents.notificationEvents.postValue(NetEvents.NOTIFICATION_READY)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // ------ 2. INSERT operations ---------- //
    // network 7
    suspend fun insertFeed(feedRequest: FeedRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.insertFeed(
                    DataProcessor.gson.toJson(feedRequest)
                )) {
                is NetworkResponse.Success -> {
                    // Handle successful response

                    NetEvents.feedEvents.postValue(NetEvents.UPDATE_FEEDS)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }
            }
        }
    }

    // network 8
    suspend fun insertOneSite(siteRequest: SiteRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.insertOneSite(DataProcessor.gson.toJson(siteRequest))) {
                is NetworkResponse.Success -> {
                    // Handle successful response

                    NetEvents.siteEvents.postValue(NetEvents.UPDATE_SITES)
                    NetEvents.feedEvents.postValue(NetEvents.UPDATE_FEEDS)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // ------ 3. UPDATE operations ---------- //
    // network 9
    suspend fun setNotification(feedid: BigInteger, notification: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.setNotification(feedid, notification)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    Repository.notification = notification

                    NetEvents.notificationEvents.postValue(NetEvents.NOTIFICATION_READY)
                    NetEvents.feedEvents.postValue(NetEvents.UPDATE_FEEDS)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // network 10
    suspend fun modifyFeed(feedid: BigInteger, title: String, description: String) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.modifyFeed(feedid, title, description)) {
                is NetworkResponse.Success -> {
                    // Handle successful response

                    NetEvents.feedEvents.postValue(NetEvents.UPDATE_FEEDS)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine
    }

    // network 10.1
    suspend fun markFeedRead(feedid: BigInteger) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.markFeedRead(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response

                    NetEvents.feedEvents.postValue(NetEvents.UPDATE_FEEDS)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine
    }

    // network 10.2
    suspend fun markAllRead() {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.markAllRead()) {
                is NetworkResponse.Success -> {
                    // Handle successful response

                    NetEvents.feedEvents.postValue(NetEvents.UPDATE_FEEDS)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine
    }

    // network 10.3
    suspend fun curateContentsFeed(feedid: BigInteger) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.deleteSite(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response

                    NetEvents.contentEvents.postValue(NetEvents.UPDATE_CONTENTS)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // ------ 4. DELETE operations ---------- //
    // network 11
    suspend fun deleteSite(siteid: BigInteger) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.deleteSite(siteid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response

                    NetEvents.siteEvents.postValue(NetEvents.UPDATE_SITES)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }

    // network 12
    suspend fun deleteFeed(feedid: BigInteger) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.deleteFeed(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response

                    NetEvents.feedEvents.postValue(NetEvents.UPDATE_FEEDS)

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                }

            } // when
        } // coroutine

    }
}
