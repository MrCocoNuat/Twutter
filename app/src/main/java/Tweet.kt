import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.ZoneId

data class Tweet @RequiresApi(Build.VERSION_CODES.O) constructor(
    val tweetJson: JSONObject,
    val id : Long = tweetJson.getLong("id"),
    val createdTimeStr : String = tweetJson.getString("created_at"),
    val relativeTimestamp : String = relativeTimestamp(createdTimeStr),
    val user : JSONObject = tweetJson.getJSONObject("user"),
    val displayname : String = user.getString("name"),
    val username : String = "@"+user.getString("screen_name"),
    val profileUrl : String = user.getString("profile_image_url_https"),
    val text : String = tweetJson.getString("text"),
    val rtCount : Int = tweetJson.getInt("retweet_count"),
    val favCount : Int = tweetJson.getInt("favorite_count"),
    ){
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun relativeTimestamp(createdTime: String): String {
            val twitterFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss xx uuuu")
            val createdDateTime : LocalDateTime = LocalDateTime.parse(createdTime, twitterFormatter) //local

            val now : LocalDateTime = LocalDateTime.now(ZoneId.of("+0000")) //match timezones, yes this is hardcoded

            val secondsAgo = createdDateTime.until(now,ChronoUnit.SECONDS)
            if (secondsAgo < 0){
                Log.w("Tweet","Encountered a tweet from the future! Timezone error?")
            }
            if (secondsAgo < 5){
                return "now"
            }
            if (secondsAgo < 60){
                return "${secondsAgo}s"
            }
            if (secondsAgo < 60*60){
                return "${secondsAgo/60}m"
            }
            if (secondsAgo < 60*60*24){
                return "${secondsAgo/60/60}h"
            }
            if (secondsAgo < 60*60*24*30){
                return "${secondsAgo/60/60/24}d"
            }
            val reverseTwitterFormatter = DateTimeFormatter.ofPattern("d MMM uu")
            return createdTime.format(reverseTwitterFormatter)
        }
    }
}