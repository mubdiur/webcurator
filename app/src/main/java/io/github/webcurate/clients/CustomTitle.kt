package io.github.webcurate.clients

import io.github.webcurate.activities.MainActivity

object CustomTitle {
    private var active = false
    private val titleList = mutableListOf<String>()
    var currentTitle = ""


    fun setTitle(title: String) {
        active = true
        titleList.add(MainActivity.nullBinding?.titleText?.text.toString())
        currentTitle = title
        MainActivity.nullBinding?.titleText?.text = title
    }

    fun resetTitle() {
        active = false
        if (titleList.isNotEmpty()) titleList.clear()
    }

    fun pop() {
        if (titleList.isNotEmpty()) {
            MainActivity.nullBinding?.titleText?.text = titleList.last()
            currentTitle = titleList.last()
            titleList.removeLast()
        }
    }
}