package io.github.webcurate.networking.models

import retrofit2.http.Field

data class FeedRequest(
    @Field("title") val title: String,
    @Field("description") val description: String,
    @Field("sites") val sites : List<SiteRequest>
)
