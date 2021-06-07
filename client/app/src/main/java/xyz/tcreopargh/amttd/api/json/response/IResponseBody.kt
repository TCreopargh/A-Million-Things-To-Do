package xyz.tcreopargh.amttd.api.json.response

import java.io.Serializable

interface IResponseBody : Serializable {
    val success: Boolean?
    val error: Int?
}
