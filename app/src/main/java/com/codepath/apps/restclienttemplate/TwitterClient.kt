package com.codepath.apps.restclienttemplate

import android.app.DownloadManager
import android.content.Context
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.codepath.oauth.OAuthBaseClient
import com.github.scribejava.apis.FlickrApi
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.api.BaseApi

/*
 *
 * This is the object responsible for communicating with a REST API.
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes:
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 *
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 *
 */
class TwitterClient(context: Context) : OAuthBaseClient(
    context, REST_API_INSTANCE, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET,
    null, String.format(
        REST_CALLBACK_URL_TEMPLATE,
        context.getString(R.string.intent_host),
        context.getString(R.string.intent_scheme),
        context.packageName,
        FALLBACK_URL
    )
) {

    companion object {
        val REST_API_INSTANCE = TwitterApi.instance() // Change this

        const val REST_URL = "https://api.twitter.com/1.1" // Change this, base API URL

        const val REST_CONSUMER_KEY =
            BuildConfig.CONSUMER_KEY // Change this inside apikey.properties

        const val REST_CONSUMER_SECRET =
            BuildConfig.CONSUMER_SECRET // Change this inside apikey.properties

        // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
        const val FALLBACK_URL =
            "https://codepath.github.io/android-rest-client-template/success.html"

        // See https://developer.chrome.com/multidevice/android/intents
        const val REST_CALLBACK_URL_TEMPLATE =
            "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end"
    }

    // this endpoint uses Twitter API v1.1
    fun getHomeTimeline(handler: JsonHttpResponseHandler, max_id: Long? = null, since_id: Long? = null){
        val apiUrl = getApiUrl("statuses/home_timeline.json")
        val params = RequestParams()
        if (max_id != null) {
            params.put("max_id",max_id)
        }
        if (since_id != null) {
            params.put("since_id",since_id)
        }
        client.get(apiUrl,params,handler)
    }








    // These endpoints use Twitter API v2 instead of v1.1
    // the home_timeline endpoint no longer exists, so followed users must be gotten first

    // what is the user's own id?
    fun getUser(handler: JsonHttpResponseHandler){
        val apiUrl = getApiUrl("users/me")
        val params = RequestParams()
        client.get(apiUrl, params, handler)
    }

    // who does the id user follow?
    fun getFollowed(id: String, handler: JsonHttpResponseHandler){
        val apiUrl = getApiUrl("users/$id/following")
        val params = RequestParams()
        client.get(apiUrl, params, handler)
    }


    // gets timeline tweets of id user
    // specify either until_id or since_id by name if you want that
    fun getTimeline(id: String, handler: JsonHttpResponseHandler, until_id: Long? = null, since_id: Long? = null) {
        val apiUrl =
            getApiUrl("users/$id/tweets")
        // Can specify query string params directly or through RequestParams.
        val params = RequestParams()
        if (until_id != null) {
            params.put("until_id",until_id)
        }
        if (since_id != null) {
            params.put("since_id",since_id)
        }

        client.get(apiUrl, params, handler)
    }

    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json")
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e val params = RequestParams("foo", "bar")
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler)
	 *    i.e client.post(apiUrl, params, handler)
	 */
}