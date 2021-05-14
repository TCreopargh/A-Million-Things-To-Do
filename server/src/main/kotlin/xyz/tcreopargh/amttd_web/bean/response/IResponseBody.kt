package xyz.tcreopargh.amttd_web.bean.response

import java.io.Serializable

interface IResponseBody : Serializable {
    var success: Boolean?
    var error: Exception?
}
