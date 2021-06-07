package xyz.tcreopargh.amttd_web.api.json.response

import xyz.tcreopargh.amttd_web.api.data.CrudType

interface ICrudResponse<T> : IResponseBody {
    val operation: CrudType?
    val entity: T?
}