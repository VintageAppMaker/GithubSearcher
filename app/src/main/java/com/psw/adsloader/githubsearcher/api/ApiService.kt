package com.psw.adsloader.githubsearcher.api

import com.psw.adsloader.githubsearcher.model.Repo
import com.psw.adsloader.githubsearcher.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.GET


interface ApiService {

    // 코루틴 내에서 간편하게 사용하기 위한 suspend를 이용한 API
    @GET("/users/{user}")
    suspend fun getUser(@Path("user") user: String): User

    // 코루틴 내에서 간편하게 사용하기 위한 suspend를 이용한 API
    @GET("/users/{user}/repos")
    suspend fun listRepos(@Path("user") user: String): List<Repo>

    // 일반적인 API
    @GET("/users/{user}/repos")
    fun listReposWithPage(@Path("user") user: String, @Query("page") page : Int): Call<List<Repo>>

}

// >> 코루틴으로 처리하기 <<
// UI처리는 반드시 LiveData로 보낸다.
// 그렇게 하지않으면 Context 변환을 하지 않으므로 앱이 종료된다.
fun IORoutine(fnProcess: suspend CoroutineScope.() -> Unit, fnError : suspend CoroutineScope.(e :Exception)->Unit){
    CoroutineScope(Dispatchers.IO).launch {
        try{
            fnProcess()
        }
        catch (e: Exception){
            fnError(e)
        }
    }
}

