package com.mubdiur.webcurator.clients

import android.view.View
import com.mubdiur.webcurator.activities.MainActivity

object CustomTitle {
    var active = true
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
        MainActivity.nullBinding?.titleText?.text = titleList.first()
        if(titleList.first() == "Feeds") MainActivity.nullBinding?.menuButton?.visibility = View.VISIBLE
    }

    fun pop() {
        MainActivity.nullBinding?.titleText?.text = titleList.last()
        currentTitle = titleList.last()
        titleList.removeLast()
    }
}