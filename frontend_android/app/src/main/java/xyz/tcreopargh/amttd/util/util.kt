/**
 * @author TCreopargh
 */
package xyz.tcreopargh.amttd.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import xyz.tcreopargh.amttd.AMTTD
import java.net.URL
import kotlin.system.exitProcess


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

fun jsonArrayOf(vararg args: Any): JsonArray {
    return JsonArray().apply {
        for (element in args) {
            when (element) {
                is Boolean -> this.add(element)
                is String -> this.add(element)
                is Number -> this.add(element)
                is Char -> this.add(element)
                is JsonElement -> this.add(element)
                else           -> this.add(element.toString())
            }
        }
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

fun doRestart(c: Context) {
    try {
        //fetch the packagemanager so we can get the default launch activity
        // (you can replace this intent with any other activity if you want
        val pm: PackageManager = c.packageManager
        //check if we got the PackageManager
        //create the intent with the default start activity for your application
        val mStartActivity = pm.getLaunchIntentForPackage(
            c.packageName
        )
        if (mStartActivity != null) {
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            //create a pending intent so the application is restarted after System.exit(0) was called.
            // We use an AlarmManager to call this intent in 100ms
            val mPendingIntentId = 223344
            val mPendingIntent = PendingIntent
                .getActivity(
                    c, mPendingIntentId, mStartActivity,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            val mgr = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] =
                mPendingIntent
            //kill the application
            exitProcess(0)
        } else {
            Log.e(AMTTD.logTag, "Was not able to restart application, mStartActivity null")
        }
    } catch (ex: Exception) {
        Log.e(AMTTD.logTag, "Was not able to restart application")
    }
}
