package io.github.webcurate.options


object OptionMenu {
    // 1-10 are the  parameter for callbacks to notify what option was chosen


    // 0 is the default context
    const val CONTEXT_FEED = 1
    const val CONTEXT_FEED_ITEM = 2

    const val CONTEXT_DEFAULT = 0

    var feedItemVisible = false
    var isVisible = false
    var notify = false


    var contextType :Int = 0

}