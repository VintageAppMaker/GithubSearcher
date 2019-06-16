package com.psw.adsloader.githubsearcher.model

sealed class GithubData

data class User(
    var login        : String,
    var public_repos : Int,
    var public_gists : Int,
    var followers    : Int,
    var following    : Int,
    var bio          : String,
    var avatar_url   : String

) : GithubData()


data class Repo(
    var id : String?,
    var name : String?,
    var full_name : String?,
    var stargazers_count : Int,
    var size             : Int,
    var description      : String?,
    var clone_url        : String,
    var owner :Owner?
) : GithubData()

data class Owner(var html_url : String)