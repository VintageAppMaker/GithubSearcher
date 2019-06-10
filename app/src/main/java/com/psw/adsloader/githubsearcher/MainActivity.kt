package com.psw.adsloader.githubsearcher


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AbsListView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    var bRefreshIng      : Boolean = false

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
        binder.rcyMain.setOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ( !recyclerView.canScrollVertically(1) ){
                    if(nNextPage > nOldPage && bRefreshIng == false){
                        toast("다음페이지를 읽습니다.")
                        bRefreshIng = true
                        loadRepoInfoWithPage()
                    }
                }
            }
        })

    }

    override fun onStart() {
        super.onStart()

        loadRepoInfo()
    }

    var nNextPage = 1
    var nOldPage  = 1
    private fun getNextPage(s: String){

        // 버그양산형 코드
        s.split("&")[0].split("=")[1].split(";")[0].replace(">", "").let{
            nOldPage  = nNextPage
            nNextPage = it.toInt()
        }

    }

    private fun loadRepoInfo() {

        api.function.listRepos("vintageappmaker").enqueue( object: Callback<List<Repo>>{

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                bRefreshIng = false
            }

            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                val repos = response.body()
                response.headers().get("Link")?.let {
                    getNextPage(it)
                }

                repos?.forEachIndexed { index, repo ->   repo.name = "${index}.${repo.name}" }
                GithubAdapter(repos!!, applicationContext)?.let{
                    adapter = it
                    binder.rcyMain.adapter = adapter
                }

                binder.data = MainActivityData().apply { title = "${adapter.mItems.size} repositories" }

                bRefreshIng = false

            }

        })

    }

    private fun loadRepoInfoWithPage() {

        api.function.listReposWithPage("vintageappmaker", nNextPage).enqueue( object: Callback<List<Repo>>{

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                val repos = response.body()

                response.headers().get("Link")?.let {
                    getNextPage(it)
                }

                repos?.let{
                    it.forEachIndexed { index, repo ->   repo.name = "${ adapter.mItems.size + index}.${repo.name}" }
                    adapter.addItems(it)
                }

                binder.data = MainActivityData().apply { title = "${adapter.mItems.size} repositories" }

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
