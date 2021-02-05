package io.github.webcurate.data

import com.google.gson.Gson
import io.github.webcurate.data.models.FeedContent
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.networking.models.FeedResponse
import io.github.webcurate.networking.models.SiteResponse
import org.jsoup.select.Elements

object DataProcessor {

    val selectionSet = mutableSetOf<Int>()

    var currentFeed: FeedResponse? = null
    var currentSite: SiteResponse? = null

    var feedCreationTitle: String = ""
    var feedCreationDescription: String = ""
    var feedCreationUrl: String = ""

    var email: String = ""
    var contentURL: String = ""


    var currentRootPosition = 0
    val gson = Gson()

    var siteModifyMode = false
    var siteAddMode = false
    var backToFeed = false

    val updatedFeeds = mutableSetOf<FeedResponse>()


    fun totalUpdates(): String {
        var count = 0
        for (feed in Repository.feedList) {
            count += feed.updates
        }
        return count.toString()
    }

    fun processFeeds() {
        updatedFeeds.clear()
        for (feed in Repository.feedList) {
            if (feed.updates > 0) {
                updatedFeeds.add(feed)
            }
        }
    }


    fun getContentsFromElements(elements: Elements): List<FeedContent> {
        val feedList = mutableListOf<FeedContent>()
        val elementsCloned = elements.clone()
        elementsCloned.forEach { it.children().remove() }
        for (i in elements.indices) {
            if (elementsCloned[i].text().isNotEmpty()) {
                // get the element to a variable
                var element = elements[i]
                // Capture slimPath and classPath
                val slimPath = mutableListOf<String>()
                val classPath = mutableListOf<String>()
                var limiter = 0
                while (element.tagName() != "body" && element.hasParent() && limiter != 10) {
                    var tagName = element.tagName()
                    slimPath.add(tagName)
                    val classSeparator = "."
                    if (element.classNames().isNotEmpty()) {
                        tagName =
                            "$tagName.${element.classNames().sorted().joinToString(classSeparator)}"
                    }
                    classPath.add(tagName)

                    limiter++
                    element = element.parent()
                }
                // add the feed content to feedList
                feedList.add(
                    FeedContent(
                        elementsCloned[i].text(),
                        slimPath.reversed(),
                        classPath.reversed()
                    )
                )
            }
        }
        return feedList
    }


//    fun filterContentStrings(
//        contentList: List<FeedContent>,
//        paths: List<PathResponse>
//    ): Set<String> {
//        val contentStrings = mutableSetOf<String>()
//        contentList.forEach {
//            if (isMatched(it, paths)) {
//                contentStrings.add(it.text)
//            }
//        }
//        return contentStrings
//    }

//    private fun isMatched(content: FeedContent, siteQueries: List<PathResponse>): Boolean {
//        var retValue = false
//        siteQueries.forEach {
//
//            if (content.slimPath.joinToString(" > ").contains(it.path)) {
//                retValue = true
//            }
//            if (content.classPath.joinToString(" > ").contains(it.path)) retValue = true
//        }
//        return retValue
//    }

    fun getSiteQueries(contentList: List<FeedContent>): Set<String> {
        val queries = mutableSetOf<String>()
        for (currentIndex in contentList.indices) {
            for (nextIndex in currentIndex until contentList.size) {
                val path1 = contentList[currentIndex].slimPath.asReversed()
                val path2 = contentList[nextIndex].slimPath.asReversed()
                val commonPath = mutableListOf<String>()
                var i = 0
                var continueLoop = true
                while (i < path1.size && i < path2.size && continueLoop) {
                    if (path1[i] == path2[i]) {
                        commonPath.add(path1[i])
                    } else continueLoop = false
                    i++
                }
                if (commonPath.isNotEmpty() && commonPath.size != 1) {
                    queries.add(commonPath.asReversed().joinToString(" > "))
                } else {
                    queries.add(path1.joinToString(" > "))
                    queries.add(path2.joinToString(" > "))
                }
            }
        }
        return queries
    }

}