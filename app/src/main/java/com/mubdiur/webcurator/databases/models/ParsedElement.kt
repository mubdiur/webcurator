package com.mubdiur.webcurator.databases.models

import org.jsoup.nodes.Element

class ParsedElement(
    private val element: Element,
    private val path: String
)