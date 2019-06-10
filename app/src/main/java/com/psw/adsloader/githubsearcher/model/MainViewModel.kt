package com.psw.adsloader.githubsearcher.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel :ViewModel(){
    var bLoading : MutableLiveData<Boolean> =  MutableLiveData<Boolean>()
    var account  : MutableLiveData<String> =  MutableLiveData<String>()

    init{
        bLoading.value = false
    }

}