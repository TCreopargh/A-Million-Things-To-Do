package xyz.tcreopargh.amttd

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import okhttp3.OkHttpClient
import xyz.tcreopargh.amttd.ui.settings.SettingsFragment
import xyz.tcreopargh.amttd.util.setNightModeAccordingToPref
import xyz.tcreopargh.amttd.util.setNightModeAutomatically
import java.util.concurrent.TimeUnit
import android.app.Application as App

/**
 * @author TCreopargh
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
        fun i18n(@StringRes stringId: Int) = context.getString(stringId)
        fun i18n(@StringRes stringId: Int, vararg objects: Any?) =
            context.getString(stringId, *objects)

        //seconds
        const val TIMEOUT = 10L
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        okHttpClient = OkHttpClient().newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
        setNightModeAccordingToPref(context)
    }
}
