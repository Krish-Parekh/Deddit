package com.krish.meme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.krish.meme.MainActivity
import com.krish.meme.R
import com.krish.meme.model.Meme
import com.krish.meme.ui.homeFragment.HomeFragment

interface ButtonClick {
    fun shareClick(meme: Meme)
    fun downloadClick(meme: Meme)

}

class MainListAdapter(private val listener: HomeFragment) :
    PagingDataAdapter<Meme, MainListAdapter.MainViewHolder>(Diff) {

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subreddit: TextView = itemView.findViewById(R.id.tvSubreddit)
        val ivMeme: ImageView = itemView.findViewById(R.id.ivMeme)
        val upCount: TextView = itemView.findViewById(R.id.upCounts)
        val author: TextView = itemView.findViewById(R.id.tvAuthor)
        val shareBtn: ImageView = itemView.findViewById(R.id.shareBtn)
        val downloadBtn: ImageView = itemView.findViewById(R.id.downloadImage)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.apply {
            val meme = getItem(position)!!
            val subName = "r/" + meme.subreddit
            subreddit.text = subName
            Glide.with(holder.itemView.context).load(meme.url).transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_error_placeholder)
                .into(ivMeme)
            upCount.text = meme.ups.toString()
            author.text = meme.author
            shareBtn.setOnClickListener {
                listener.shareClick(meme)
            }
            downloadBtn.setOnClickListener {
                listener.downloadClick(meme)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return MainViewHolder(view)
    }

    object Diff : DiffUtil.ItemCallback<Meme>() {
        override fun areItemsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem.url == newItem.url && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem == newItem
        }
    }

}