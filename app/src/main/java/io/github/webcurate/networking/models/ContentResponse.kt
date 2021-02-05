package io.github.webcurate.networking.models

import java.math.BigInteger
import java.sql.Timestamp

data class ContentResponse(
    val id: BigInteger,
    val feedid: BigInteger,
    val siteid: BigInteger,
    val text: String,
    val source: String,
    val new: Int,
    val unnotified: Int,
    val timestamp: Timestamp
)
