package io.github.webcurate.data

import android.database.Observable
import androidx.lifecycle.MutableLiveData

object NetEvents {
    const val DEFAULT = 0
    const val UPDATE_FEEDS = 1
    const val CONTENTS_READY = 2
    const val SITES_READY = 3
    const val FEEDS_READY = 4
    const val TOPIC_READY = 5
    const val NOTIFICATION_READY = 5
    const val UPDATE_SITES = 6
    const val TOKEN_READY = 7
    const val UPDATE_CONTENTS = 8
    // feedResponse
    val feedEvents: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    // siteResponse
    val siteEvents: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    // contentResponse
    val contentEvents: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    // Topic response
    val topicEvents: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    // Notification response
    val notificationEvents: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    // auth events
    val authEvents: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
}