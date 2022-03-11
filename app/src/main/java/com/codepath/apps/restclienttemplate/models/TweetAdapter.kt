package com.codepath.apps.restclienttemplate.models

import Tweet
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.R

class TweetItemAdapter(private val context: Context,
                       private val tweets: MutableList<Tweet>) : Adapter<TweetItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val profileView = itemView.findViewById<ImageView>(R.id.ivProfile)
        private val textView = itemView.findViewById<TextView>(R.id.tvText)
        private val timeView = itemView.findViewById<TextView>(R.id.tvTime)
        private val displaynameView = itemView.findViewById<TextView>(R.id.tvDisplayname)
        private val usernameView = itemView.findViewById<TextView>(R.id.tvUsername)

        fun bind(tweet : Tweet){
            Glide.with(context)
                .load(tweet.profileUrl)
                .circleCrop()
                .into(profileView)
            textView.text = tweet.text
            displaynameView.text = tweet.displayname
            usernameView.text = tweet.username
            timeView.text = Tweet.relativeTimestamp(tweet.createdTimeStr)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tweet = tweets[position]
        holder.bind(tweet)
    }

}