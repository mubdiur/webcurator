package com.mubdiur.webcurator.databases.models

class FeedContent(
    val text: String,
    val slimPath: List<String>,
    val classPath: List<String>,
    var selected: Boolean = false
)