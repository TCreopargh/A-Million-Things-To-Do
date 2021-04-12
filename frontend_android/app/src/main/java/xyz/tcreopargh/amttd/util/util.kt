/**
 * @author TCreopargh
 */
package xyz.tcreopargh.amttd.util

import android.text.*
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import xyz.tcreopargh.amttd.AMTTD
import java.net.URL

const val PACKAGE_NAME = "xyz.tcreopargh.amttd"
const val PACKAGE_NAME_DOT = "$PACKAGE_NAME."

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
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

fun JsonObject.map(vararg args: Pair<String, Any>) {
    for (pair in args) {
        when (pair.second) {
            is Boolean -> addProperty(pair.first, pair.second as Boolean)
            is String -> addProperty(pair.first, pair.second as String)
            is Number -> addProperty(pair.first, pair.second as Number)
            is Char -> addProperty(pair.first, pair.second as Char)
            is JsonElement -> add(pair.first, pair.second as JsonElement)
            else           -> addProperty(pair.first, pair.second.toString())
        }
    }
}

fun jsonObjectOf(vararg args: Pair<String, Any>): JsonObject {
    return JsonObject().apply {
        map(*args)
    }
}

fun JsonObject.toResponseBody(): ResponseBody = this.toString().toResponseBody(JSON)
fun JsonObject.toRequestBody(): RequestBody = this.toString().toRequestBody(JSON)

fun i18n(@StringRes resId: Int) = AMTTD.i18n(resId)
fun i18n(@StringRes resId: Int, vararg objects: Any?) = AMTTD.i18n(resId, *objects)

fun URL.withPath(path: String): URL {
    val normalizedPath = if (path.startsWith("/")) path else "/$path"
    return URL(this.toString() + normalizedPath)
}
