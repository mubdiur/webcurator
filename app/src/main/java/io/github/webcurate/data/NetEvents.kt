package io.github.webcurate.data

import androidx.lifecycle.MutableLiveData

object NetEvents {
    const val DEFAULT = 0
    const val UPDATE_FEEDS = 1
    const val CONTENTS_READY = 2
    const val SITES_READY = 3
    const val FEEDS_READY = 4
    const val TOPIC_READY = 5
    const val NOTIFICATION_READY = 6
    const val UPDATE_SITES = 7
    const val TOKEN_READY = 8
    const val UPDATE_CONTENTS = 9
    const val SITE_DELETED = 10
    const val CURATE_CONTENTS = 11
    const val CONTENTS_CURATED = 12
    const val CONTENTS_INVALID = 13
    const val LOAD_URL = 14
    const val HTML_READY = 15
    const val NAME_CHANGED = 16

    // feedResponse
    val feedEvents = MutableLiveData<Int>()
    // siteResponse
    val siteEvents = MutableLiveData<Int>()
    // contentResponse
    val contentEvents =  MutableLiveData<Int>()
    // Topic response
    val topicEvents = MutableLiveData<Int>()
    // Notification response
    val notificationEvents = MutableLiveData<Int>()
    // auth events
    val authEvents = MutableLiveData<Int>()
    // browser events
    val browserEvents = MutableLiveData<Int>()
    // browser events
    val htmlEvents = MutableLiveData<Int>()
}