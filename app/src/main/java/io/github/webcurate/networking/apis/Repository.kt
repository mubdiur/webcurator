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
    var feedList = mutableSetOf<FeedResponse>()
    var siteList = mutableSetOf<SiteResponse>()
    var contentList = mutableSetOf<ContentResponse>()
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
                    feedList.clear()
                    feedList.addAll(response.body)
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.FEEDS_READY
                    }


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
                    siteList.clear()
                    siteList.addAll(response.body)
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.siteEvents.value = NetEvents.SITES_READY
                    }

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
                    contentList.clear()
                    contentList.addAll(response.body)
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.contentEvents.value = NetEvents.CONTENTS_READY
                    }

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
                    topic = ""
                    topic = response.body
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.topicEvents.value = NetEvents.TOPIC_READY
                    }


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
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.notificationEvents.value = NetEvents.NOTIFICATION_READY
                    }


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
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                    }
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
    suspend fun insertOneSite(feedid: BigInteger, siteRequest: SiteRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.insertOneSite(feedid, DataProcessor.gson.toJson(siteRequest))) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    // review this
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                    }
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
                    // review this
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.notificationEvents.value = NetEvents.NOTIFICATION_READY
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                    }


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
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                    }


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
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                    }


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
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                    }


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
                NetworkService.service.curateContentsFeed(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.contentEvents.value = NetEvents.CONTENTS_CURATED
                    }

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
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.siteEvents.value = NetEvents.SITE_DELETED
                    }


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
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                        NetEvents.contentEvents.value = NetEvents.CONTENTS_INVALID
                    }


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
