package xyz.tcreopargh.amttd.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.api.data.IUser


/**
 * @author TCreopargh
 *
 * A task designed explicitly for loading user avatars
 *
 * onSuccess and onFailure callbacks are run at the calling thread.
 */
abstract class LoadUserAvatarTask(user: IUser, lifecycleOwner: LifecycleOwner) :
    Runnable {

    private val bitmap = MutableLiveData<Bitmap?>(null)
    private val exception = MutableLiveData<Exception?>(null)

    init {
        bitmap.observe(lifecycleOwner) {
            if (it != null) {
                onSuccess(it)
            }
        }
        exception.observe(lifecycleOwner) {
            if (it != null) {
                onFailure(it)
            }
        }
    }

    abstract fun onSuccess(bitmap: Bitmap)
    abstract fun onFailure(e: Exception)

    private val url = rootUrl.withPath("/user/avatar/" + user.uuid.toString())

    /**
     * Use [start] if you want to run this in a new thread.
     */
    override fun run() {
        try {
            val httpRequest = okHttpRequest(url)
                .get()
                .build()
            val response = AMTTD.okHttpClient.newCall(httpRequest).execute()
            val stream = response.body?.byteStream()
            bitmap.postValue(BitmapFactory.decodeStream(stream))
        } catch (e: Exception) {
        }
    }

    fun start() {
        Thread(this).start()
    }
}