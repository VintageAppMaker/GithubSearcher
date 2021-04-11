package com.psw.adsloader.githubsearcher.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel :ViewModel(){
    var bLoading : MutableLiveData<Boolean> =  MutableLiveData()
    var account  : MutableLiveData<String>  =  MutableLiveData()
    var title    : MutableLiveData<String>  =  MutableLiveData()
    var lst      : MutableLiveData< List<GithubData> > = MutableLiveData()

    var message   : MutableLiveData< String > = MutableLiveData()

    init{
        bLoading.value = false
    }

}