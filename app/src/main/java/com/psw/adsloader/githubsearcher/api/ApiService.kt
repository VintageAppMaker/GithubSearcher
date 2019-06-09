package com.psw.adsloader.githubsearcher.api

import com.psw.adsloader.githubsearcher.model.Repo
import com.psw.adsloader.githubsearcher.model.User
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.GET


interface ApiService {

    @GET("/users/{user}")
    fun getUser(@Path("user") user: String): Call<User>

    @GET("/users/{user}/repos")
    fun listRepos(@Path("user") user: String): Call<List<Repo>>

}
