package xyz.tcreopargh.amttd.util

import android.util.Log
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.common.bean.request.IActionRequest
import xyz.tcreopargh.amttd.common.bean.response.IActionResponse
import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.exception.AmttdException
import java.lang.reflect.Type
import java.net.URL

/**
 * @author TCreopargh
 *
 * A Task to send CRUD data to the server.
 *
 * Note: The callback methods are **NOT** executed in the main thread.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class CrudTask<Entity, out Request : IActionRequest<Entity>, in Response : IActionResponse<Entity>>(
    val request: Request,
    val path: String,
    val responseType: Type,
    val printLogs: Boolean = true,
    val isPathAbsolute: Boolean = false
) {

    abstract fun onSuccess(entity: Entity?)

    abstract fun onFailure(e: Exception)

    open fun onCompleted() {}

    open fun onResponse(response: Response) {}

    open fun execute() {
        Thread {
            try {
                val httpRequest = okhttp3.Request.Builder()
                    .post(
                        request.toJsonRequest()
                    ).url(
                        if (isPathAbsolute) URL(path) else rootUrl.withPath(path)
                    )
                    .build()
                val response = AMTTD.okHttpClient.newCall(httpRequest).execute()
                val body = response.body?.string() ?: "{}"
                if(enableJsonDebugging) {
                    Log.i(AMTTD.logTag, body)
                }
                // Don't simplify this
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
            // Make sure the server side is done processing
            onCompleted()
        }.start()
    }
}