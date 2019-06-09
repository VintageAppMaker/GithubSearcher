package com.psw.adsloader.githubsearcher.model

data class User(
    var login        : String,
    var public_repos : Int,
    var public_gists : Int,
    var followers    : Int,
    var following    : Int
)


data class Repo(
    var id : String?,
    var name : String?,
    var full_name : String?,
    var stargazers_count : Int,
    var size             : Int,
    var description      : String?,
    var owner :Owner?
)

data class Owner(var html_url : String)