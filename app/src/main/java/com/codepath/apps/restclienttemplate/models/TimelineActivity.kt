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
import java.security.KeyStore

class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient

    private lateinit var rvTimeline: RecyclerView
    private lateinit var swipeContainer: SwipeRefreshLayout


    val timelineTweets = mutableListOf<Tweet>()
    private var oldestTweetId: Long = -1
    // handle when to load more for infinite scroll
    var lastKnownSize = 0
    var loadMoreReady = false

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
            renewTimeline()
        }

        rvTimeline = findViewById(R.id.rvTimeline)

        rvTimeline.adapter = TweetItemAdapter(this,
            timelineTweets,
            object : TweetItemAdapter.OutOfItemsListener{
                override fun outOfItems(previousSize: Int) {
                    if (loadMoreReady) {
                        Log.i("TimelineActivity", "Complying: loading more items")
                        loadMoreReady = false
                        extendTimeline()
                    }
                    else if (previousSize > lastKnownSize){
                        Log.i("TimelineActivity","Ready to load more items next call")
                        loadMoreReady = true
                        lastKnownSize = previousSize
                    }
                    else {
                        Log.i("TimelineActivity","Refused to load more items")
                    }
                }
            })
        rvTimeline.layoutManager = LinearLayoutManager(this)

        renewTimeline()
    }

    //trashes current contents
    fun renewTimeline() {
        client.getHomeTimeline(object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e("TimelineActivity", "Error from API $statusCode: $response")
                reflectTimelineChanges()
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                timelineTweets.clear()
                lastKnownSize = 0
                for (i in 0 until json.jsonArray.length()) {
                    timelineTweets.add(Tweet(json.jsonArray.getJSONObject(i)))
                }
                reflectTimelineChanges()
            }
        })
    }

    // does not trash current contents, only appends to end
    fun extendTimeline() {
        client.getHomeTimeline(object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e("TimelineActivity", "Error from API $statusCode: $response")
                reflectTimelineChanges()
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                loadMoreReady = false
                for (i in 0 until json.jsonArray.length()) {
                    timelineTweets.add(Tweet(json.jsonArray.getJSONObject(i)))
                }
                reflectTimelineChanges()
            }
        }, max_id = oldestTweetId)
    }

    fun reflectTimelineChanges() {
        if (timelineTweets.size > 0)
            oldestTweetId = timelineTweets[timelineTweets.size - 1].id
        rvTimeline.adapter?.notifyDataSetChanged()
        swipeContainer.isRefreshing = false
    }










/*
    // All Unused!

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
    */

}