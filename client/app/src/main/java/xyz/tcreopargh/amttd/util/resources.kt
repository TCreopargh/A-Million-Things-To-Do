package xyz.tcreopargh.amttd.util

import com.google.gson.Gson
import okhttp3.Request
import java.net.URL
import kotlin.random.Random

/**
 * @author TCreopargh
 */

val gson: Gson = Gson()

val random: Random = Random.Default

var sessionId: String? = null

fun okHttpRequest(url: URL): Request.Builder {
    val id = sessionId
    return if (id != null) {
        Request.Builder().addHeader("cookie", id).url(url)
    } else {
        Request.Builder().url(url)
    }
}

fun okHttpRequest(relativeUrl: String): Request.Builder =
    okHttpRequest(rootUrl.withPath(relativeUrl))