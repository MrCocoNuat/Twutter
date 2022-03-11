package com.codepath.apps.restclienttemplate.models

import Tweet
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.R
import com.codepath.apps.restclienttemplate.TwitterApplication
import com.codepath.apps.restclienttemplate.TwitterClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONObject

class TimelineActivity : AppCompatActivity() {

    // if false, actually make API calls
    val dummy = false

    lateinit var client : TwitterClient
    private lateinit var rvTimeline : RecyclerView
    private lateinit var swipeContainer : SwipeRefreshLayout
    val timelineTweets = mutableListOf<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        // starts chain of async calls
        // using Twitter API v2, not v1.1
        // TODO incomplete
        //getUser()

        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            getTimeline()
        }

        rvTimeline = findViewById(R.id.rvTimeline)
        rvTimeline.adapter = TweetItemAdapter(this,timelineTweets)
        rvTimeline.layoutManager = LinearLayoutManager(this)

        getTimeline()
    }

    fun getTimeline(){
        client.getHomeTimeline(object : JsonHttpResponseHandler(){
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e("TimelineActivity","Error from API $statusCode: $response")
                swipeContainer.isRefreshing=false
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                timelineTweets.clear()
                for (i in 0 until json.jsonArray.length()){
                    timelineTweets.add(Tweet(json.jsonArray.getJSONObject(i)))
                }
                rvTimeline.adapter?.notifyDataSetChanged()
                swipeContainer.isRefreshing = false
            }
        })
    }









    // get the logged in user's id and call getFollowed on that id
    fun getUser() {
        if (dummy) {
            val id = "946537847280033792"
            getFollowed(id)
        } else {
            client.getUser(object : JsonHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.e("TimelineActivity", "Error from Twitter API $statusCode: $response")
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                    Log.i("TimelineActivity", "OK from Twitter API $statusCode")
                    val id = json.jsonObject.getJSONObject("data").getString("id")
                    Log.i("TimelineActivity","Got user id $id")
                    getFollowed(id)
                }
            })
        }
    }

    fun getFollowed(id: String){
        if (dummy){
            return
        } else {
            client.getFollowed(id, object:JsonHttpResponseHandler(){
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.e("TimelineActivity", "Error from Twitter API $statusCode: $response")
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                    val followedUsers = json.jsonObject.getJSONArray("data")
                    val followedIds = mutableListOf<String>()
                    //JSONArray is quite a under-implemented class, no listOf, no map, ... must do indexing?
                    for (i in 0 until followedUsers.length()){
                        followedIds.add(followedUsers.getJSONObject(i).getString("id"))
                    }
                    followedIds.add(id) //add self
                    assembleTimeline(followedIds)
                }

            })
        }

    }

    fun assembleTimeline(followedIds: List<String>){
        for (userId in followedIds){
            getTweetsBy(userId)
        }
        // TODO, a further callback to be notified when all user's tweets are added to timelineIds, then bind them to recycler
    }

    fun getTweetsBy(id: String){
        client.getTimeline(id, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
               Log.e("TimelineActivity","Error from Twitter API $statusCode: $response")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                val tweetIds = json.jsonObject.getJSONArray("data")
                for (i in 0 until tweetIds.length()){
                   //timelineIds.add(tweetIds.getJSONObject(i).getString("id"))
                }
                Log.i("TimelineActivity","Got tweets by user $id")
            }

        })
    }
}