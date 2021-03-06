package xyz.tcreopargh.amttd

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.StringRes
import okhttp3.OkHttpClient
import xyz.tcreopargh.amttd.ui.settings.SettingsFragment
import xyz.tcreopargh.amttd.util.getLocalizedResources
import xyz.tcreopargh.amttd.util.runOnLocal
import xyz.tcreopargh.amttd.util.setNightModeAccordingToPref
import java.util.*
import java.util.concurrent.TimeUnit
import android.app.Application as App


/**
 * @author TCreopargh
 *
 * Main class of application
 */
class AMTTD : App() {

    companion object {
        @SuppressLint("StaticFieldLeak") // It's safe to do this in an application subclass
        lateinit var context: Context
            private set
        lateinit var okHttpClient: OkHttpClient
            private set
        const val logTag = "AMTTD"
        fun i18n(@StringRes stringId: Int) =
            getLocalizedResources(context, Locale(language)).getString(stringId)

        fun i18n(@StringRes stringId: Int, vararg objects: Any?) =
            getLocalizedResources(context, Locale(language)).getString(stringId, *objects)

        //seconds
        const val TIMEOUT = 30L
        var language: String = "en"

        lateinit var versionName: String
        var versionCode: Long = 0
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        okHttpClient = OkHttpClient().newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
        setNightModeAccordingToPref(context)
        if (runOnLocal) {
            Log.i(
                logTag,
                "A Million Things To Do is connecting to local server. Remote changes will not take effect."
            )
        } else {
            Log.i(
                logTag,
                "A Million Things To Do is connecting to remote server. Local changes will not take effect."
            )
        }
        language = context.getSharedPreferences(
            SettingsFragment.PREF_FILE_NAME,
            MODE_PRIVATE
        )?.getString(
            "language",
            "default"
        ) ?: "default"
        if (language == "default") {
            language = Locale.getDefault().language
        }

        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = pInfo.versionName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                versionCode = pInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(logTag, e.stackTraceToString())
        }
    }
}
