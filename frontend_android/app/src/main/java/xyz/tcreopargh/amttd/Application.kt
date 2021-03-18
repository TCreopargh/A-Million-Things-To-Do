package xyz.tcreopargh.amttd

import android.annotation.SuppressLint
import android.content.Context
import android.app.Application as App

/**
 * @author TCreopargh
 */
class Application : App() {

    companion object {
        @SuppressLint("StaticFieldLeak") // It's safe to do this in an application subclass
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
