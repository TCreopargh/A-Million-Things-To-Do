package xyz.tcreopargh.amttd.common.bean.response

import java.io.Serializable

interface IResponseBody : Serializable {
    val success: Boolean?
    val error: Int?
}
