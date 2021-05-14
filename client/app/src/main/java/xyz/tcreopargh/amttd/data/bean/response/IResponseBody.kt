package xyz.tcreopargh.amttd.data.bean.response

import java.io.Serializable

interface IResponseBody : Serializable {
    var success: Boolean?
    var error: Int?
}
