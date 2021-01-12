package io.github.webcurator.databases

import io.github.webcurator.databases.models.SiteQuery
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

object Curator {

    // Returns elements by filtering them with paths
    fun getContents(elements: Elements, queries: List<SiteQuery>): Set<String> {
        val contents = mutableSetOf<String>()
        queries.forEach { query ->
            val elementsByQueries = elements.select(query.path).select("*")
            for (i in elementsByQueries.indices) {
                if (elementsByQueries[i].ownText().isNotBlank()) {
                    contents.add(elementsByQueries[i].ownText())
                }
            }
        }
        return contents
    }


    // generate a list of paths with the specified selection from elements
    fun generateQueries(elements: Elements, selection: List<Int>): MutableSet<String> {
        // Common paths are generated from this list of paths
        val listPaths = mutableListOf<Path>()

        selection.forEach { index ->
            val path = Path()
            var el = elements[index]
            while (el.tagName() != "body" && el.hasParent()) {
                // Get the path unit and add it to this path
                val pathUnit = PathUnit()
                pathUnit.setValues(el)
                path.pathUnits.add(pathUnit)
                el = el.parent()
            }
            // after one element is done add it's path to the list of all paths
            val reversedPath = Path()
            reversedPath.pathUnits.addAll(path.pathUnits.reversed())
            listPaths.add(reversedPath)
        }
        return queriesFromPaths(listPaths)
    } // generate queries


    private fun queriesFromPaths(paths: MutableList<Path>): MutableSet<String> {
        val queries = mutableSetOf<String>()
        paths.forEachIndexed { currentIndex, path ->
            for (restPathIndex in currentIndex + 1 until paths.size) {
                val matchedPath = Path()

                val path1 = path.pathUnits
                val path2 = paths[restPathIndex].pathUnits

                var startPosition = 0
                var startCount = 0
                while (startPosition < path1.size && startPosition < path2.size) {
                    if (path1[startPosition].matched(path2[startPosition])) {
                        startPosition++
                        startCount++
                    } else break
                }

                // now reverse them
                val path1reversed = path.pathUnits.reversed()
                val path2reversed = paths[restPathIndex].pathUnits.reversed()

                var endPosition = 0
                var endCount = 0
                while (endPosition < path1.size - startCount && endPosition < path2.size - startCount) {
                    if (path1reversed[endPosition].matched(path2reversed[endPosition])) {
                        endPosition++
                        endCount++
                    } else break
                }
                val endMatch = Path()
                for (i in 1..endCount) {
                    endMatch.pathUnits.add(path1reversed[i - 1])
                }

                // two paths have been compared

                // add start
                for (i in 1..startCount) {
                    matchedPath.pathUnits.add(path1[i - 1])
                }
                // add end
                matchedPath.pathUnits.addAll(endMatch.pathUnits.reversed())
                if (matchedPath.pathUnits.isNotEmpty()) {
                    queries.add(matchedPath.toQuery())
                }
            }
        }
        return queries
    } // queries from paths

} // curator object


class Path {
    var pathUnits = mutableListOf<PathUnit>()

    fun toQuery(): String {
        var query = ""
        pathUnits.forEach {
            query = if (query.isEmpty()) {
                it.query()
            } else "$query>${it.query()}"
        }
        return query
    }
}

class PathUnit {
    private var tagName = ""
    private var idName = ""
    private var classes = mutableSetOf<String>()

    fun setValues(element: Element) {
        this.tagName = element.tagName()
        this.idName = element.id()
        this.classes = element.classNames().sorted().toMutableSet()
    }

    fun query(): String {
        var queryString = ""
        classes.forEach {
            if (it.isNotEmpty())
                queryString = "$queryString.$it"
        }
        if (idName.isNotBlank()) idName = "#$idName"
        queryString = "${this.tagName}$idName$queryString"
        return queryString
    }

    fun matched(pathUnit: PathUnit): Boolean {
        return (pathUnit.tagName == this.tagName
                && pathUnit.idName == this.idName
                && pathUnit.classes == this.classes)
    }
}