package io.github.webcurate.networking.models

import java.math.BigInteger

data class ContentResponse(
    val siteid: BigInteger,
    val text: String,
    val source: String
)
