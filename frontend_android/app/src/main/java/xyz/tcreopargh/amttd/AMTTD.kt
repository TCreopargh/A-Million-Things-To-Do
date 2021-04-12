package xyz.tcreopargh.amttd

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import okhttp3.OkHttpClient
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
        val logTag = "AMTTD"
        fun i18n(@StringRes stringId: Int)  = context.getString(stringId)
        fun i18n(@StringRes stringId: Int, vararg objects: Any?)  = context.getString(stringId, *objects)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        okHttpClient = OkHttpClient()
    }
}
