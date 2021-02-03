package io.github.webcurate.networking.apis

import com.haroldadmin.cnradapter.NetworkResponse
import io.github.webcurate.activities.MainActivity
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
    var htmlResponse = HtmlResponse("")


    // network 0
    suspend fun getHtml(url: String) {
        htmlResponse = HtmlResponse("")
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getHtml(url)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    htmlResponse = response.body
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.htmlEvents.value = NetEvents.HTML_READY
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine

    }

    // ------ 1. FETCH operations ---------- //
    // network 1
    suspend fun getUserFeeds() {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getUserFeeds()) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    feedList.clear()
                    feedList.addAll(response.body)
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.FEEDS_READY
                        NetEvents.feedDeleteEvents.value = NetEvents.FEEDS_READY
                        NetEvents.feedNotifyEvents.value = NetEvents.FEEDS_READY
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine

    }

    // network 2
    suspend fun getSitesForFeed(feedid: BigInteger) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getSitesForFeed(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    siteList.clear()
                    siteList.addAll(response.body)
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.siteEvents.value = NetEvents.SITES_READY
                        MainActivity.stopLoadingAnimation()
                    }

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine
    }

    // network 3
    suspend fun getContentsForFeed(feedid: BigInteger) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getContentsForFeed(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    contentList.clear()
                    contentList.addAll(response.body)
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.contentEvents.value = NetEvents.CONTENTS_READY
                        MainActivity.stopLoadingAnimation()
                    }

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine

    }


    // network 5
    suspend fun getTopic() {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.getTopic()) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    topic = ""
                    topic = response.body
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.topicEvents.value = NetEvents.TOPIC_READY
                        MainActivity.stopLoadingAnimation()
                    }


                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine

    }


    // ------ 2. INSERT operations ---------- //
    // network 7
    suspend fun insertFeed(feedRequest: FeedRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.insertFeed(
                    DataProcessor.gson.toJson(feedRequest)
                )) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
            }
        }
    }

    // network 8
    suspend fun insertOneSite(feedid: BigInteger, siteRequest: SiteRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.insertOneSite(
                    feedid,
                    DataProcessor.gson.toJson(siteRequest)
                )) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    // review this
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine

    }

    // ------ 3. UPDATE operations ---------- //
    // network 9
    suspend fun setNotification(feedid: BigInteger, notification: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.setNotification(feedid, notification)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    // review this
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.notificationEvents.value = NetEvents.NOTIFICATION_READY
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                        NetEvents.feedNotifyEvents.value = NetEvents.UPDATE_FEEDS // used in manage
                        MainActivity.stopLoadingAnimation()
                    }


                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine
    }

    // network 10
    suspend fun modifyFeed(feedid: BigInteger, title: String, description: String) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.modifyFeed(feedid, title, description)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                        MainActivity.stopLoadingAnimation()
                    }


                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine
    }

    // network 10.1
    suspend fun markFeedRead(feedid: BigInteger) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.markFeedRead(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                        MainActivity.stopLoadingAnimation()
                    }


                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine
    }

    // network 10.2
    suspend fun markAllRead() {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.markAllRead()) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                        MainActivity.stopLoadingAnimation()
                    }


                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine
    }

    // network 10.3
    suspend fun curateContentsFeed(feedid: BigInteger) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.curateContentsFeed(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.contentEvents.value = NetEvents.CONTENTS_CURATED
                        MainActivity.stopLoadingAnimation()
                    }

                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
            } // when
        } // coroutine
    }

    // ------ 4. DELETE operations ---------- //
    // network 11
    suspend fun deleteSite(siteid: BigInteger) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.deleteSite(siteid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.siteEvents.value = NetEvents.SITE_DELETED
                        MainActivity.stopLoadingAnimation()
                    }


                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine

    }

    // network 12
    suspend fun deleteFeed(feedid: BigInteger) {
        CoroutineScope(Dispatchers.Main).launch {
            MainActivity.startLoadingAnimation()
        }
        CoroutineScope(Dispatchers.IO).launch {
            when (val response =
                NetworkService.service.deleteFeed(feedid)) {
                is NetworkResponse.Success -> {
                    // Handle successful response
                    CoroutineScope(Dispatchers.Main).launch {
                        NetEvents.feedEvents.value = NetEvents.UPDATE_FEEDS
                        NetEvents.feedDeleteEvents.value = NetEvents.UPDATE_FEEDS
                        NetEvents.contentEvents.value = NetEvents.CONTENTS_INVALID
                        MainActivity.stopLoadingAnimation()
                    }


                }
                is NetworkResponse.ServerError -> {
                    // Handle server error
                    println(response.body?.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.NetworkError -> {
                    // Handle network error
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }
                is NetworkResponse.UnknownError -> {
                    // Handle other errors
                    println(response.error.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.stopLoadingAnimation()
                    }
                }

            } // when
        } // coroutine

    }
}
