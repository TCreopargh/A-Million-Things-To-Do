package xyz.tcreopargh.amttd_web.common.bean.response

import java.io.Serializable

/**
 * Serialization objects that go from server to client.
 * All responses may potentially contain error, so please make
 * sure you check for the error on client.
 */
interface IResponseBody : Serializable {
    var success: Boolean?
    var error: Int?
}
