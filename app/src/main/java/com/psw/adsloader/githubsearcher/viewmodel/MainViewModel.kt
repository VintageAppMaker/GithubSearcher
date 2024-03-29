package com.psw.adsloader.githubsearcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psw.adsloader.githubsearcher.MainActivity
import com.psw.adsloader.githubsearcher.api.IORoutine
import com.psw.adsloader.githubsearcher.api.api
import com.psw.adsloader.githubsearcher.model.GithubData
import com.psw.adsloader.githubsearcher.model.Repo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel :ViewModel(){
    var bLoading : MutableLiveData<Boolean> =  MutableLiveData()
    var account  : MutableLiveData<String>  =  MutableLiveData()
    var title    : MutableLiveData<String>  =  MutableLiveData()
    var lst      : MutableLiveData< List<GithubData> > = MutableLiveData()

    var message   : MutableLiveData< String > = MutableLiveData()

    val FIRST_PAGE  =  1
    var nNextPage   =  FIRST_PAGE
    val IS_END_PAGE = -1 // -1이면 end

    // repo 갯수
    var totalCount = 0

    init{
        bLoading.value = false
    }

    fun loadUserInfo() {
        nNextPage = FIRST_PAGE
        bLoading.postValue(true)

        // 코투틴과 Retrofit 사용방법을 위한 예제
        // UI처리는 반드시 LiveData로 보낸다.
        // 그렇게 하지않으면 Context간의 차이로 App이 종료됨
        IORoutine({
            val u = api.github.getUser(account.value.toString())
            if(u == null) return@IORoutine

            // 데이터처리
            var items = mutableListOf<GithubData>().apply{
                add(u as GithubData)
            }

            totalCount = u.public_repos

            // UI에 전송
            lst.postValue(items)
        }, {
            bLoading.postValue(false)
            message.postValue("$it")
        })

    }

    fun loadRepoInfo() {
        nNextPage = FIRST_PAGE
        bLoading.postValue(true)

        IORoutine({
            val l = api.github.listRepos(account.value.toString())
            if(l == null){
                bLoading.postValue(false)
                toNextPageWithEnd(true)
                return@IORoutine
            }

            if(l.size < 1){
                bLoading.postValue(false)
                toNextPageWithEnd(true)
                return@IORoutine
            }

            toNextPageWithEnd()

            l?.forEachIndexed { index, repo ->
                repo.name = "${repo.name}"
            }

            lst.postValue(l)
            title.postValue("$totalCount repositories")
            bLoading.postValue(false)

        }, {
            bLoading.postValue(false)
            message.postValue("$it")
        })

    }

    fun loadRepoInfoWithPage() {
        bLoading.postValue(true)

        // 코루틴을 사용하지 않는방법
        api.github.listReposWithPage(account.value.toString(), nNextPage).enqueue( object:
            Callback<List<Repo>> {

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                bLoading.postValue(false)
            }

            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                val repos = response.body()

                if(repos == null){
                    bLoading.postValue(false)
                    toNextPageWithEnd(true)
                    return
                }

                if(repos.size < 1){
                    bLoading.postValue(false)
                    toNextPageWithEnd(true)
                    return
                }

                toNextPageWithEnd()

                lst.postValue(repos)

                repos?.let{
                    it.forEachIndexed { index, repo ->   repo.name = "${repo.name}" }
                }

                title.postValue("${totalCount} repositories")
                bLoading.postValue(false)

            }

        })
    }

    private fun toNextPageWithEnd(bIsEnd : Boolean = false ){
        if(nNextPage != IS_END_PAGE) nNextPage++
        if( bIsEnd )
            nNextPage = IS_END_PAGE
    }

}