package xyz.tcreopargh.amttd.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.common.data.IUser
import java.io.InputStream
import java.net.HttpURLConnection


/**
 * @author TCreopargh
 * A task designed explicitly for loading user avatars
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

    private val url = rootUrl.withPath("/user/avatar?uuid=" + user.uuid.toString())

    /**
     * Use [start] if you want to run this in a new thread.
     */
    override fun run() {
        try {
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            bitmap.postValue(BitmapFactory.decodeStream(input))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun start() {
        Thread(this).start()
    }
}