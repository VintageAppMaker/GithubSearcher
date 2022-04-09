package com.psw.adsloader.githubsearcher


import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.psw.adsloader.githubsearcher.adapter.GithubAdapter
import com.psw.adsloader.githubsearcher.databinding.ActivityMainBinding
import com.psw.adsloader.githubsearcher.util.toast
import com.psw.adsloader.githubsearcher.view.MainActivityData

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.psw.adsloader.githubsearcher.viewmodel.MainViewModel
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.psw.adsloader.githubsearcher.model.GithubData
import com.psw.adsloader.githubsearcher.util.setBottomSystemBarColor
import com.psw.adsloader.githubsearcher.util.setOverSystemMenu


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
                        viewmodel.loadRepoInfoWithPage()
                    }
                }
            }
        })

        GithubAdapter(mutableListOf<GithubData>(), applicationContext)?.let{
            adapter = it
            binder.rcyMain.adapter = adapter
        }

        viewmodel.account.postValue("google")

        setUpSystemArea()

    }

    private fun setUpSystemArea() {
        // Systembar 침범
        setOverSystemMenu()

        // 하단 네비게이션 메뉴 색상변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBottomSystemBarColor(getColor(R.color.colorBottomSystem))
        }

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

    companion object {
        val FIRST_PAGE  =  1
        var nNextPage   =  FIRST_PAGE
        val IS_END_PAGE = -1 // -1이면 end
    }


}
