package com.psw.adsloader.githubsearcher.model

data class Repo(
    var id : String?,
    var name : String?,
    var full_name : String?,
    var owner :Owner?
)

data class Owner(var html_url : String)