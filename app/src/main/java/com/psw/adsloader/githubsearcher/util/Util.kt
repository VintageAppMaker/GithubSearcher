package com.psw.adsloader.githubsearcher.util

import android.content.Context
import android.widget.Toast

// Toast
fun Context.toast(s : String){
    Toast.makeText(this, s, Toast.LENGTH_LONG).show()
}


