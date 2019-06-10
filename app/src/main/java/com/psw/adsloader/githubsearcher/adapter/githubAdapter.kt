package com.psw.adsloader.githubsearcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psw.adsloader.githubsearcher.R
import com.psw.adsloader.githubsearcher.model.Repo
import kotlinx.android.synthetic.main.item_github_list.view.*

class GithubAdapter(val items : List<Repo>, val context: Context) : RecyclerView.Adapter<githubHolder>() {

    var mItems : MutableList <Repo> = items.toMutableList()

    fun addItems( its : List<Repo>){
        mItems.addAll(its)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): githubHolder {
        return githubHolder(LayoutInflater.from(context).inflate(R.layout.item_github_list, parent, false))
    }

    override fun onBindViewHolder(holder: githubHolder, position: Int) {
        holder?.txtName.text = mItems.get(position).name
        holder?.txtSize.text = mItems.get(position).size.toString()
        holder?.txtStar.text = mItems.get(position).stargazers_count.toString()
        holder?.txtDescription.text = mItems.get(position).description
    }

}

class githubHolder (view: View) : RecyclerView.ViewHolder(view) {
    var txtName : TextView = view.txtName
    var txtSize : TextView = view.txtSize
    var txtStar : TextView = view.txtStar
    var txtDescription : TextView = view.txtDescription

}