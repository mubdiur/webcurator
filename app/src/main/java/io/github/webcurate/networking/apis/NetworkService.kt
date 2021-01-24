package io.github.webcurate.networking.apis

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.101:8321")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .build()

    val service: NetworkInterface = retrofit.create(NetworkInterface::class.java)
}