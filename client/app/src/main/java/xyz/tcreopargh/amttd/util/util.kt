/**
 * @author TCreopargh
 */

@file:Suppress("unused")

package xyz.tcreopargh.amttd.util

import android.app.Activity
import android.content.Intent
import android.text.*
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import xyz.tcreopargh.amttd.AMTTD
import java.net.URL
import java.text.DateFormat
import java.util.*
import kotlin.random.Random


const val PACKAGE_NAME = "xyz.tcreopargh.amttd"
const val PACKAGE_NAME_DOT = "$PACKAGE_NAME."
const val GROUP_URI_PREFIX = "amttd://group/"

fun isGroupUri(uri: String) = uri.startsWith(GROUP_URI_PREFIX, false)
fun getGroupUri(path: String) = "$GROUP_URI_PREFIX$path"
fun getGroupInvitationCode(amttdUri: String): String? {
    if (!isGroupUri(amttdUri)) return null
    return amttdUri.substring(GROUP_URI_PREFIX.length)
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun Calendar.format(isTimeInDay: Boolean = false): String = if (isTimeInDay) {
    DateFormat.getTimeInstance(
        DateFormat.MEDIUM,
        AMTTD.context.resources.configuration.locales.get(0)
    ).format(this.time)
} else {
    DateFormat.getDateInstance(
        DateFormat.MEDIUM,
        AMTTD.context.resources.configuration.locales.get(0)
    ).format(this.time)
}

fun Random.nextString(
    length: Int,
    dictionary: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
): String {
    return StringBuilder().apply {
        repeat(length) {
            append(dictionary.random())
        }
    }.toString()
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

operator fun Spannable.plus(other: Spannable): Spannable {
    return SpannableStringBuilder(this).append(other)
}

fun SpannableString.setColor(@ColorInt color: Int): SpannableString = apply {
    setSpan(
        ForegroundColorSpan(color),
        0,
        length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun <T> T.toJsonRequest(): RequestBody {
    return gson.toJson(this, object : TypeToken<T>() {}.type).toRequestBody(mediaTypeJson)
}

fun <T> T.toJson(): JsonElement {
    return gson.toJsonTree(this, object : TypeToken<T>() {}.type)
}

fun <T> T.toJsonString(): String {
    return gson.toJson(this, object : TypeToken<T>() {}.type)
}

fun JsonObject.map(vararg args: Pair<String, Any>) {
    for (pair in args) {
        when (pair.second) {
            is Boolean     -> addProperty(pair.first, pair.second as Boolean)
            is String      -> addProperty(pair.first, pair.second as String)
            is Number      -> addProperty(pair.first, pair.second as Number)
            is Char        -> addProperty(pair.first, pair.second as Char)
            is Enum<*>     -> addProperty(pair.first, pair.second.toString())
            is UUID        -> addProperty(pair.first, pair.second.toString())
            is JsonElement -> add(pair.first, pair.second as JsonElement)
            else           -> add(pair.first, pair.second.toJson())
        }
    }
}

fun jsonObjectOf(vararg args: Pair<String, Any>): JsonObject {
    return JsonObject().apply {
        map(*args)
    }
}

fun jsonArrayOf(vararg args: Any): JsonArray {
    return JsonArray().apply {
        for (element in args) {
            when (element) {
                is Boolean     -> this.add(element)
                is String      -> this.add(element)
                is Number      -> this.add(element)
                is Char        -> this.add(element)
                is Enum<*>     -> this.add(element.toString())
                is UUID        -> this.add(element.toString())
                is JsonElement -> this.add(element)
                else           -> this.add(element.toJson())
            }
        }
    }
}

fun JsonObject.toResponseBody(): ResponseBody = this.toString().toResponseBody(mediaTypeJson)
fun JsonObject.toRequestBody(): RequestBody = this.toString().toRequestBody(mediaTypeJson)

fun i18n(@StringRes resId: Int) = AMTTD.i18n(resId)
fun getColor(@ColorRes colorId: Int) = AMTTD.context.getColor(colorId)
fun i18n(@StringRes resId: Int, vararg objects: Any?) = AMTTD.i18n(resId, *objects)

fun URL.withPath(path: String): URL {
    val normalizedPath = if (path.startsWith("/")) path else "/$path"
    return URL(this.toString() + normalizedPath)
}

fun doRestart(c: Activity) {
    c.finish()
    c.startActivity(Intent(c, c.javaClass))
    c.finishAffinity()
}
