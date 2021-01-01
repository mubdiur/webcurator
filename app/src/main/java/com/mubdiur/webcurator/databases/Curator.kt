package com.mubdiur.webcurator.databases

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Curator private constructor() {
    companion object {








        // Returns elements by filtering them with paths
        fun getContents(elements: Elements, queries: List<String>): Set<String> {
            val contents = mutableSetOf<String>()
            queries.forEach { query ->
                val elementsByQueries = elements.select(query).select("*")
                for(i in elementsByQueries.indices) {
                    if(elementsByQueries[i].ownText().isNotBlank()) {
                        contents.add(elementsByQueries[i].ownText())
                    }
                }
            }
            return contents
        }












        // generate a list of paths with the specified selection from elements
        fun generateQueries(elements: Elements, selection: List<Int>): List<String> {
            val elementQueries = mutableSetOf<String>()
            // Common paths are generated from this list of paths
            val listPaths = mutableListOf<Path>()

            selection.forEach { index ->
                val thisPath = Path()
                var el = elements[index]
                while (el.tagName() != "body" && el != null) {
                    // Get the path unit and add it to this path
                    val pathUnit = PathUnit()
                    pathUnit.setValues(el)
                    thisPath.pathUnits.add(pathUnit)
                    el = el.parent()
                }
                // after one element is done add it's path to the list of all paths
                val reversedPath = Path()
                reversedPath.pathUnits.addAll(thisPath.pathUnits.reversed())
                //reversedPath.breakage = thisPath.breakage
                listPaths.add(reversedPath)

            }

            // Generate matched paths from the list of all paths
            val matched = mutableMapOf<Int, Boolean>() // tracks the matched paths
            for (position in 0 until listPaths.size) {
                if (matched[position] != true) {
                    var bestPath = Path()
                    // if there's no next meaning only one selected
                    if(listPaths.size==1) {
                        bestPath = listPaths[0]
                        if(bestPath.toQuery().isNotBlank()) elementQueries.add(bestPath.toQuery())
                    }
                    // from next till last, find all matches
                    for (nextPositions in position + 1 until listPaths.size) {
                        if (matched[nextPositions] != true) {
                            // check
                            val matchedPath = Path()
                            var k = 0
                            while (k < listPaths[position].pathUnits.size && k < listPaths[nextPositions].pathUnits.size) {
                                if (listPaths[position].pathUnits[k].matched(listPaths[nextPositions].pathUnits[k]))
                                    matchedPath.pathUnits.add(listPaths[position].pathUnits[k])
                                else break
                                k++
                            }
                            // if the matched path is smaller in size
                            // then match the reverse for just remaining items
                            //var breakage = -1
//                            if (matchedPath.pathUnits.size < listPaths[position].pathUnits.size) {
//                                breakage = matchedPath.pathUnits.size
//                            }

                            val remaining = listPaths[position].pathUnits.size - matchedPath.pathUnits.size
                            val reversedCurrentPath = listPaths[position].pathUnits.reversed().subList(0, remaining)
                            val reversedNextPath = listPaths[nextPositions].pathUnits.reversed()
                            val reversedMatch = mutableListOf<PathUnit>()
                            k = 0
                            while (k < reversedCurrentPath.size && k < reversedNextPath.size) {
                                if (reversedCurrentPath[k].matched(reversedNextPath[k]))
                                    reversedMatch.add(reversedCurrentPath[k])
                                else break
                                k++
                            }
                            matchedPath.pathUnits.addAll(reversedMatch.reversed())

                            if (matchedPath.pathUnits.size > bestPath.pathUnits.size) {

                                bestPath = matchedPath
                                //bestPath.breakage = breakage
                                matched[position] = true
                            }
                            if(bestPath.toQuery().isNotBlank()) elementQueries.add(bestPath.toQuery())
                        }
                    }
                }
            }
            // Add the leftovers that found no matches
            for (index in listPaths.indices) {
                if (matched[index]!=true) {
                    if(listPaths[index].toQuery().isNotBlank()) elementQueries.add(listPaths[index].toQuery())
                }
            }
            return elementQueries.sortedWith { lhs, rhs ->
                val l1 = lhs.length
                val l2 = rhs.length
                when {
                    l1 < l2 -> -1
                    l1 > l2 -> 1
                    else -> 0
                }
            }
        } // generate Paths
    } // companion object
} // curator class

class Path {
    var pathUnits = mutableListOf<PathUnit>()

    //var breakage = -1
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
        this.classes = element.classNames()
    }

    fun query(): String {
        var queryString = ""
        classes.forEach {
            if(it.isNotEmpty())
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