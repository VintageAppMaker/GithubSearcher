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
import com.psw.adsloader.githubsearcher.util.toast
import com.psw.adsloader.githubsearcher.view.MainActivityData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.psw.adsloader.githubsearcher.model.MainViewModel
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.psw.adsloader.githubsearcher.api.IORoutine
import com.psw.adsloader.githubsearcher.model.GithubData


class MainActivity : AppCompatActivity() {

    lateinit var binder  : ActivityMainBinding
    lateinit var adapter : GithubAdapter
    lateinit var viewmodel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpUI()
    }

    private fun setUpUI() {

        UIObserver()

        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binder.bottomNav.setOnNavigationItemReselectedListener {
            when (it.itemId ){
                (R.id.menu_search)   -> {
                    adapter.clearItems()
                    viewmodel.title.postValue("${adapter.mItems.size} repositories")
                    askUser()
                }
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

        GithubAdapter(mutableListOf<GithubData>(), applicationContext)?.let{
            adapter = it
            binder.rcyMain.adapter = adapter
        }

        viewmodel.account.postValue("google")

    }

    private fun UIObserver() {
        viewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewmodel.apply {
            bLoading.observe(this@MainActivity, Observer<Boolean> {
                // 바인딩된 bindData에 값을 넣었을 때...
                binder.prgLoading.visibility = if (it) View.VISIBLE else View.GONE
            })

            account.observe(this@MainActivity, Observer<String> {
                // 이름이 바뀌면 바로검색
                loadUserInfo()
                // 레포정보를 추가
                loadRepoInfo()
            })

            title.observe(this@MainActivity, Observer<String> {
                binder.data = MainActivityData().apply { title = it }
            })

            // 코루틴에서 추가함
            lst.observe(this@MainActivity, Observer {
                // 사용자 정보를 추가
                adapter.addItems(it)
            })

            message.observe(this@MainActivity, Observer {
                toast(it)
            })
        }
    }

    private fun askUser() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("깃헙아이디")

        val input = EditText(this)
        input.inputType  = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
        input.imeOptions = EditorInfo.IME_ACTION_DONE

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

    private fun loadUserInfo() {
        nNextPage = FIRST_PAGE
        viewmodel.bLoading.postValue(true)

        // 코투틴과 Retrofit 사용방법을 위한 예제
        // UI처리는 반드시 LiveData로 보낸다.
        // 그렇게 하지않으면 Context간의 차이로 App이 종료됨
        IORoutine({
            val u = api.function.getUser(viewmodel.account.value.toString())
            if(u == null) return@IORoutine

            // 데이터처리
            var items = mutableListOf<GithubData>().apply{
                add(u as GithubData)
            }

            // UI에 전송
            viewmodel.lst.postValue(items)
        }, {
            viewmodel.bLoading.postValue(false)
            viewmodel.message.postValue("$it")
        })

    }

    private fun loadRepoInfo() {
        nNextPage = FIRST_PAGE
        viewmodel.bLoading.postValue(true)

        IORoutine({
            val l = api.function.listRepos(viewmodel.account.value.toString())
            if(l == null){
                viewmodel.bLoading.postValue(false)
                toNextPageWithEnd(true)
                return@IORoutine
            }

            if(l.size < 1){
                viewmodel.bLoading.postValue(false)
                toNextPageWithEnd(true)
                return@IORoutine
            }

            toNextPageWithEnd()

            l?.forEachIndexed { index, repo ->   repo.name = "${index}.${repo.name}" }

            viewmodel.lst.postValue(l)
            viewmodel.title.postValue("${adapter.mItems.size -1} repositories")
            viewmodel.bLoading.postValue(false)

        }, {
            viewmodel.bLoading.postValue(false)
            viewmodel.message.postValue("$it")
        })

    }

    private fun loadRepoInfoWithPage() {
        viewmodel.bLoading.postValue(true)

        // 코루틴을 사용하지 않는방법
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

                viewmodel.title.postValue("${adapter.mItems.size - 1} repositories")
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
