package com.psw.adsloader.githubsearcher.api

import com.psw.adsloader.githubsearcher.model.Repo
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.GET


interface ApiService {

    @GET("/users/{user}/repos")
    fun listReposStr(@Path("user") user: String): Call<String>

    @GET("/users/{user}/repos")
    fun listRepos(@Path("user") user: String): Call<List<Repo>>

}
