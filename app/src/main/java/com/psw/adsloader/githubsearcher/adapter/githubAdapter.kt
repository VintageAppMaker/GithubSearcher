package com.psw.adsloader.githubsearcher.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.psw.adsloader.githubsearcher.R
import com.psw.adsloader.githubsearcher.model.GithubData
import com.psw.adsloader.githubsearcher.model.Repo
import com.psw.adsloader.githubsearcher.model.User
import kotlinx.android.synthetic.main.item_github_list.view.*
import kotlinx.android.synthetic.main.item_github_list.view.txtName
import kotlinx.android.synthetic.main.item_github_list2.view.*
import kotlinx.android.synthetic.main.item_github_list3.view.*


class GithubAdapter(val items : List<GithubData>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mItems : MutableList <GithubData> = items.toMutableList()

    fun addItems( its : List<GithubData>){
        mItems.addAll(its)
        notifyDataSetChanged()
    }

    fun clearItems(){
        mItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            TYPE_ONE   -> {githubHolder(LayoutInflater.from(context).inflate(R.layout.item_github_list, parent, false))}
            TYPE_TWO   -> {githubHolder2(LayoutInflater.from(context).inflate(R.layout.item_github_list2, parent, false))}
            TYPE_THREE -> {githubHolder3(LayoutInflater.from(context).inflate(R.layout.item_github_list3, parent, false))}
            else -> {githubHolder(LayoutInflater.from(context).inflate(R.layout.item_github_list, parent, false))}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType){
            TYPE_ONE -> { (holder as githubHolder).apply {
                var item = mItems.get(position) as Repo
                bind(context,  item)
            }}

            TYPE_TWO -> { (holder as githubHolder2).apply {
                var item = mItems.get(position) as Repo
                bind(context, item)
            }}

            TYPE_THREE -> { (holder as githubHolder3).apply {
                var item = mItems.get(position) as User
                bind(context, item)
            }}
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (mItems.get(position)){
            is Repo -> {
                var item = mItems.get(position) as Repo
                if( item.stargazers_count > 0) TYPE_ONE else TYPE_TWO
            }
            else    -> {TYPE_THREE}
        }
    }

    companion object {
        private const val TYPE_ONE   = 0
        private const val TYPE_TWO   = 1
        private const val TYPE_THREE = 3
    }
}



class githubHolder (view: View) : RecyclerView.ViewHolder(view) {
    var txtNumber : TextView = view.txtNumber
    var txtName   : TextView = view.txtName
    var txtSize   : TextView = view.txtSize
    var txtStar   : TextView = view.txtStar

    // XML에서 같은 ID가 있을 경우, 에러가 발생한다.
    // findViewById()를 이용하는 것이 바람직함
    var txtDescription : TextView = view.txtDescription

    lateinit var vHolder : View
    init {
        vHolder = view
    }

    fun bind(context : Context, item : Repo){
        txtNumber.text = item.id
        txtName.text = item.name
        txtSize.text = item.size.toString()
        txtStar.text = item.stargazers_count.toString()

        txtDescription.apply{
            text = if ( item.description != null )  item.description else "설명없음"
        }

        vHolder.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.clone_url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }
}

class githubHolder2 (view: View) : RecyclerView.ViewHolder(view) {
    var txtName : TextView = view.txtName
    var txtCloneUrl : TextView = view.txtCloneUrl

    lateinit var vHolder : View
    init {
        vHolder = view
    }

    // XML에서 같은 ID가 있을 경우, 에러가 발생한다.
    // findViewById()를 이용하는 것이 바람직함
    var txtDescription : TextView = view.txtDescription2

    fun bind(context : Context,item : Repo){
        txtName.text = item.name
        txtCloneUrl.text = item.clone_url
        txtDescription.text = if ( item.description != null )  item.description else "설명없음"

        vHolder.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.clone_url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}

class githubHolder3 (view: View) : RecyclerView.ViewHolder(view) {
    var txtName   :  TextView  = view.txtAccount
    var txtPublic :  TextView  = view.txtPublic
    var txtGist   :  TextView  = view.txtGist
    var txtBio    :  TextView  = view.txtBio
    var imgProfile : ImageView = view.imgProfile

    var txtFollowers : TextView = view.txtFollowers
    var txtFolling   : TextView = view.txtFollowing


    fun bind(context : Context,item : User){
        txtName.text    = item.login

        txtPublic.text     = item.public_repos.toString()
        txtGist.text       = item.public_gists.toString()
        txtFollowers.text  = item.followers.toString()
        txtFolling.text    = item.following.toString()

        txtBio.text     = item.bio

        if(item.avatar_url == "null") return
        Glide.with(context)
            .load(item.avatar_url)
            .fitCenter()
            .into(imgProfile)
        imgProfile
    }
}