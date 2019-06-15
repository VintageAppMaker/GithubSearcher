package com.psw.adsloader.githubsearcher


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.psw.adsloader.githubsearcher.model.MainViewModel
import android.R.string.cancel
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    lateinit var binder  : ActivityMainBinding
    lateinit var adapter : GithubAdapter
    lateinit var viewmodel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpUI()
    }

    private fun setUpUI() {

        viewmodel =  ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewmodel.bLoading.observe(this, Observer<Boolean> {
            // 바인딩된 bindData에 값을 넣었을 때...
            binder.prgLoading.visibility = if( it ) View.VISIBLE else View.GONE
        })

        viewmodel.account.observe(this, Observer<String> {
            // 이름이 바뀌면 바로검색
            loadRepoInfo()
        })


        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binder.bottomNav.setOnNavigationItemReselectedListener {
            when (it.itemId ){
                (R.id.menu_search)   -> { askUser() }
                else -> {}
            }
        }

        binder.rcyMain.layoutManager = LinearLayoutManager(this)
        binder.rcyMain.setOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ( !recyclerView.canScrollVertically(1) ){
                    if(viewmodel.bLoading.value == false){

                        if(nNextPage == IS_END_PAGE) return
                        toast("다음페이지를 읽습니다.")
                        loadRepoInfoWithPage()
                    }
                }
            }
        })

        viewmodel.account.postValue("square")

    }

    private fun askUser() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("깃헙아이디")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("예",
            { dialog, which -> viewmodel.account.postValue(input.text.toString())  })
        builder.setNegativeButton("취소",
            { dialog, which -> dialog.cancel() })

        builder.show()

    }

    private fun toNextPageWithEnd(bIsEnd : Boolean = false ){
         if(nNextPage != IS_END_PAGE ) nNextPage++
         if( bIsEnd )
             nNextPage = IS_END_PAGE
    }

    private fun loadRepoInfo() {
        nNextPage = FIRST_PAGE
        viewmodel.bLoading.postValue(true)

        api.function.listRepos(viewmodel.account.value.toString()).enqueue( object: Callback<List<Repo>>{

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                viewmodel.bLoading.postValue(false)
            }

            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                val repos = response.body()

                if(repos == null){
                    viewmodel.bLoading.postValue(false)
                    toNextPageWithEnd(true)
                    return
                }

                if(repos.size < 1){
                    viewmodel.bLoading.postValue(false)
                    toNextPageWithEnd(true)
                    return
                }

                toNextPageWithEnd()

                repos?.forEachIndexed { index, repo ->   repo.name = "${index}.${repo.name}" }
                GithubAdapter(repos!!, applicationContext)?.let{
                    adapter = it
                    binder.rcyMain.adapter = adapter
                }

                binder.data = MainActivityData().apply { title = "${adapter.mItems.size} repositories" }

                viewmodel.bLoading.postValue(false)

            }

        })

    }

    private fun loadRepoInfoWithPage() {
        viewmodel.bLoading.postValue(true)

        api.function.listReposWithPage(viewmodel.account.value.toString(), nNextPage).enqueue( object: Callback<List<Repo>>{

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                viewmodel.bLoading.postValue(false)
            }

            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                val repos = response.body()

                if(repos == null){
                    viewmodel.bLoading.postValue(false)
                    toNextPageWithEnd(true)
                    return
                }

                if(repos.size < 1){
                    viewmodel.bLoading.postValue(false)
                    toNextPageWithEnd(true)
                    return
                }

                toNextPageWithEnd()

                repos?.let{
                    it.forEachIndexed { index, repo ->   repo.name = "${ adapter.mItems.size + index}.${repo.name}" }
                    adapter.addItems(it)
                }

                binder.data = MainActivityData().apply { title = "${adapter.mItems.size} repositories" }
                viewmodel.bLoading.postValue(false)

            }

        })

    }

    companion object {
        val FIRST_PAGE  =  1
        var nNextPage   =  FIRST_PAGE
        val IS_END_PAGE = -1 // -1이면 end
    }


}
