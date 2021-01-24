package io.github.webcurate.networking.models

import java.math.BigInteger

data class FeedResponse(
    val id: BigInteger,
    val title: String,
    val description: String,
    val updates: Int,
    val notification: Int
)
