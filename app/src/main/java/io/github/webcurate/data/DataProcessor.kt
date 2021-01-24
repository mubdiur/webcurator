package io.github.webcurate.data

import com.google.gson.Gson
import io.github.webcurate.data.models.FeedContent
import io.github.webcurate.networking.apis.Repository
import io.github.webcurate.networking.models.FeedResponse
import io.github.webcurate.networking.models.SiteResponse
import org.jsoup.select.Elements

object DataProcessor {
    var currentFeed: FeedResponse? = null
    var currentSite: SiteResponse? = null

    var feedCreationTitle: String = ""
    var feedCreationDescription: String = ""
    var feedCreationUrl: String = ""
    var html: String = ""
    var email: String = ""
    var currentRootPosition = 0
    val gson = Gson()
    var siteModifyMode = false

    val updatedList = mutableSetOf<FeedResponse>()

    fun totalUpdateCount(): Int {
        var count = 0
        for (feed in Repository.feedList) {
            count += feed.updates
        }
        return count
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
                // compare current one to the rest
                val commonShortPath = mutableListOf<String>()
                val commonLongPath = mutableListOf<String>()

                var current = contentList[currentIndex].slimPath.size - 1
                var next = contentList[nextIndex].slimPath.size - 1
                var matchIndex = -1
                while (current >= 0 && next >= 0) {
                    if (contentList[currentIndex].slimPath[current] == contentList[nextIndex].slimPath[next]) {
                        matchIndex = current
                        current--
                        next--
                    } else break
                }

                if (matchIndex != -1) {
                    for (i in matchIndex until contentList[currentIndex].slimPath.size) {
                        commonShortPath.add(contentList[currentIndex].slimPath[i])
                    }
                }

                current = contentList[currentIndex].classPath.size - 1
                next = contentList[nextIndex].classPath.size - 1
                matchIndex = -1
                while (current >= 0 && next >= 0) {
                    if (contentList[currentIndex].classPath[current] == contentList[nextIndex].classPath[next]) {
                        matchIndex = current
                        current--
                        next--
                    } else break
                }

                if (matchIndex != -1) {
                    for (i in matchIndex until contentList[currentIndex].classPath.size) {
                        commonLongPath.add(contentList[currentIndex].classPath[i])
                    }
                }

                if (commonLongPath.isNotEmpty()) queries.add(commonLongPath.joinToString(" > "))
                if (commonShortPath.isNotEmpty()) queries.add(commonShortPath.joinToString(" > "))
            }
        }
        return queries
    }

}