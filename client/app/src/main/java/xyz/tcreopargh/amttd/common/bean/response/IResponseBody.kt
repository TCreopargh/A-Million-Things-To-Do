package xyz.tcreopargh.amttd.common.bean.response

import java.io.Serializable

interface IResponseBody : Serializable {
    var success: Boolean?
    var error: Int?
}
