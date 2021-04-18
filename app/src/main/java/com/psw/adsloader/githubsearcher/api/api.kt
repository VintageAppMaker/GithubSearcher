package com.psw.adsloader.githubsearcher.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object api {
    val BASE = "https://api.github.com"
    val builder = OkHttpClient.Builder()
        .addInterceptor( HttpLoggingInterceptor().apply {

            // Debug시에 모든 패킷을 덤프
            setLevel(HttpLoggingInterceptor.Level.BODY)

        } )

    val github: GithubReq
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build()

            return retrofit.create<GithubReq>(GithubReq::class.java!!)
        }
}