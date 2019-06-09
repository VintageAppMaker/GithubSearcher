package com.psw.adsloader.githubsearcher


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.psw.adsloader.githubsearcher.adapter.GithubAdapter
import com.psw.adsloader.githubsearcher.api.api
import com.psw.adsloader.githubsearcher.databinding.ActivityMainBinding
import com.psw.adsloader.githubsearcher.model.Repo
import com.psw.adsloader.githubsearcher.model.User
import com.psw.adsloader.githubsearcher.util.toast
import com.psw.adsloader.githubsearcher.view.MainActivityData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var binder  : ActivityMainBinding
    lateinit var adapter : GithubAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpUI()
    }

    private fun setUpUI() {

        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binder.bottomNav.setOnNavigationItemReselectedListener {
            when (it.itemId ){
                (R.id.menu_search)   -> { loadUserInfo() }
                else -> {}
            }
        }

        binder.rcyMain.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()

        loadRepoInfo()
    }

    private fun loadRepoInfo() {

        api.function.listRepos("vintageappmaker").enqueue( object: Callback<List<Repo>>{

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                val repos = response.body()
                run{
                    binder.data = MainActivityData().apply { title = "${repos?.size} repositories" }
                }

                //repos?.forEach { toast("${it.name!!} - ${it.owner!!.html_url}" )  }
                GithubAdapter(repos!!, applicationContext)?.let{
                    binder.rcyMain.adapter = it
                }
            }
        })

    }

    private fun loadUserInfo() {

        api.function.getUser("vintageappmaker").enqueue( object: Callback<User>{

            override fun onFailure(call: Call<User>, t: Throwable) {

            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                toast("팔로워:${user!!.followers}  팔로잉:${user!!.following}\nrepositories:${user!!.public_repos}\nlogin:${user!!.login}")

            }
        })

    }
}
