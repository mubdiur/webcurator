package io.github.webcurate.networking.apis

import com.haroldadmin.cnradapter.NetworkResponse
import io.github.webcurate.data.AuthManager
import io.github.webcurate.networking.models.ContentResponse
import io.github.webcurate.networking.models.ErrorResponse
import io.github.webcurate.networking.models.FeedResponse
import io.github.webcurate.networking.models.SiteResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.math.BigInteger

interface NetworkInterface {

    // ------ 1. FETCH operations ---------- //
    // network 1
    @FormUrlEncoded
    @POST("/")
    suspend fun getUserFeeds(
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "getUserFeeds"
    ): NetworkResponse<List<FeedResponse>, ErrorResponse>

    // network 2
    @FormUrlEncoded
    @POST("/")
    suspend fun getSitesForFeed(
        @Field("feedid") feedid: BigInteger,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "getSitesForFeed"
    ): NetworkResponse<List<SiteResponse>, ErrorResponse>

    // network 3
    @FormUrlEncoded
    @POST("/")
    suspend fun getContentsForFeed(
        @Field("feedid") feedid: BigInteger,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "getContentsForFeed"
    ): NetworkResponse<List<ContentResponse>, ErrorResponse>

    // network 5
    @FormUrlEncoded
    @POST("/")
    suspend fun getTopic(
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "getTopic"
    ): NetworkResponse<String, ErrorResponse>


    // ------ 2. INSERT operations ---------- //
    // network 7
    @FormUrlEncoded
    @POST("/")
    suspend fun insertFeed(
        @Field("feed") feed: String,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "insertFeed"
    ): NetworkResponse<Unit, ErrorResponse>

    // network 8
    @FormUrlEncoded
    @POST("/")
    suspend fun insertOneSite(
        @Field("feedid") feedid: BigInteger,
        @Field("site") site: String,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "insertOneSite"
    ): NetworkResponse<Unit, ErrorResponse>

    // ------ 3. UPDATE operations ---------- //
    // network 9
    @FormUrlEncoded
    @POST("/")
    suspend fun setNotification(
        @Field("feedid") feedid: BigInteger,
        @Field("notification") notification: Int,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "setNotification"
    ): NetworkResponse<Unit, ErrorResponse>

    // network 10
    @FormUrlEncoded
    @POST("/")
    suspend fun modifyFeed(
        @Field("feedid") feedid: BigInteger,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "modifyFeed"
    ): NetworkResponse<Unit, ErrorResponse>
    // network 10.1
    @FormUrlEncoded
    @POST("/")
    suspend fun markFeedRead(
        @Field("feedid") feedid: BigInteger,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "markFeedRead"
    ): NetworkResponse<Unit, ErrorResponse>
    // network 10.2
    @FormUrlEncoded
    @POST("/")
    suspend fun markAllRead(
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "markAllRead"
    ): NetworkResponse<Unit, ErrorResponse>
    // network 10.3
    @FormUrlEncoded
    @POST("/")
    suspend fun curateContentsFeed(
        @Field("feedid") feedid: BigInteger,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "curateContentsFeed"
    ): NetworkResponse<Unit, ErrorResponse>
    // ------ 4. DELETE operations ---------- //
    // network 11
    @FormUrlEncoded
    @POST("/")
    suspend fun deleteSite(
        @Field("siteid") siteid: BigInteger,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "deleteSite"
    ): NetworkResponse<Unit, ErrorResponse>

    // network 12
    @FormUrlEncoded
    @POST("/")
    suspend fun deleteFeed(
        @Field("feedid") feedid: BigInteger,
        @Field("token") token: String = AuthManager.idToken,
        @Field("operation") operation: String = "deleteFeed"
    ): NetworkResponse<Unit, ErrorResponse>


}