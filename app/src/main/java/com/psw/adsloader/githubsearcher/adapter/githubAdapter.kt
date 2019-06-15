package com.psw.adsloader.githubsearcher.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psw.adsloader.githubsearcher.R
import com.psw.adsloader.githubsearcher.model.Repo
import kotlinx.android.synthetic.main.item_github_list.view.*
import kotlinx.android.synthetic.main.item_github_list.view.txtName
import kotlinx.android.synthetic.main.item_github_list2.view.*


class GithubAdapter(val items : List<Repo>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mItems : MutableList <Repo> = items.toMutableList()

    fun addItems( its : List<Repo>){
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
            TYPE_ONE -> {githubHolder(LayoutInflater.from(context).inflate(R.layout.item_github_list, parent, false))}
            TYPE_TWO -> {githubHolder2(LayoutInflater.from(context).inflate(R.layout.item_github_list2, parent, false))}
            else -> {githubHolder(LayoutInflater.from(context).inflate(R.layout.item_github_list, parent, false))}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType){
            TYPE_ONE -> { (holder as githubHolder).apply {
                bind(context, mItems.get(position))
            }}

            TYPE_TWO -> { (holder as githubHolder2).apply {
                bind(context, mItems.get(position))
            }}
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if(mItems.get(position).stargazers_count > 0) TYPE_ONE else TYPE_TWO
    }

    companion object {
        private const val TYPE_ONE = 0
        private const val TYPE_TWO = 1
    }
}



class githubHolder (view: View) : RecyclerView.ViewHolder(view) {
    var txtName : TextView = view.txtName
    var txtSize : TextView = view.txtSize
    var txtStar : TextView = view.txtStar
    var txtDescription : TextView = view.txtDescription

    fun bind(context : Context, item : Repo){
        txtName.text = item.name
        txtSize.text = item.size.toString()
        txtStar.text = item.stargazers_count.toString()

        txtDescription.apply{
            text = if ( item.description != null )  item.description else "설명없음"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.clone_url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

    }
}

class githubHolder2 (view: View) : RecyclerView.ViewHolder(view) {
    var txtName : TextView = view.txtName
    var txtCloneUrl : TextView = view.txtCloneUrl

    fun bind(context : Context,item : Repo){
        txtName.text = item.name
        txtCloneUrl.text = item.clone_url
        txtCloneUrl.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.clone_url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}