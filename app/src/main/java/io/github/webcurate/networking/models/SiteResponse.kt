package io.github.webcurate.networking.models

import java.math.BigInteger

data class SiteResponse(
    val id: BigInteger,
    val feedid: BigInteger,
    val url: String
)
