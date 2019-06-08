package com.psw.adsloader.githubsearcher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psw.adsloader.githubsearcher.api.api
import com.psw.adsloader.githubsearcher.model.Repo
import com.psw.adsloader.githubsearcher.util.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        api.function.listRepos("vintageappmaker").enqueue( object: Callback<List<Repo>>{

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                val repos = response.body()
                toast(repos!![0].full_name!!)
            }
        })
    }
}
