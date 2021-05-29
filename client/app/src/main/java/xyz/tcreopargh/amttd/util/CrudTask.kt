package xyz.tcreopargh.amttd.util

import android.util.Log
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.common.bean.request.ICrudRequest
import xyz.tcreopargh.amttd.common.bean.response.ICrudResponse
import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.exception.AmttdException
import java.lang.reflect.Type
import java.net.URL

/**
 * @author TCreopargh
 *
 * A Task to send CRUD data to the server.
 *
 * To use this, extend this class and override the methods.
 *
 * Note: The callback methods are **NOT** executed in the main thread.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class CrudTask<Entity, out Request : ICrudRequest<Entity>, in Response : ICrudResponse<Entity>>(
    val request: Request,
    val path: String,
    val responseType: Type,
    val printLogs: Boolean = true,
    val isPathAbsolute: Boolean = false
) : Runnable {

    abstract fun onSuccess(entity: Entity?)

    abstract fun onFailure(e: Exception)

    open fun onCompleted() {}

    open fun onResponse(response: Response) {}

    /**
     * Use [start] if you want to run this in a new thread.
     */
    override fun run() {
        try {
            val httpRequest =
                okHttpRequest(if (isPathAbsolute) URL(path) else rootUrl.withPath(path))
                    .post(
                        request.toJsonRequest()
                    )
                    .build()
            val response = AMTTD.okHttpClient.newCall(httpRequest).execute()
            val body = response.body?.string() ?: "{}"
            if (enableJsonDebugging) {
                Log.i(AMTTD.logTag, body)
            }
            val result: Response =
                gson.fromJson(
                    body,
                    responseType
                )
            onResponse(result)
            if (result.success != true) {
                throw AmttdException.getFromErrorCode(result.error)
            }
            onSuccess(
                result.entity
                    ?: if (request.operation == CrudType.READ) throw AmttdException(
                        AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND
                    ) else null
            )
        } catch (e: Exception) {
            if (printLogs) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
            }
            onFailure(e)
        }
        onCompleted()
    }

    open fun start() {
        Thread(this).start()
    }
}