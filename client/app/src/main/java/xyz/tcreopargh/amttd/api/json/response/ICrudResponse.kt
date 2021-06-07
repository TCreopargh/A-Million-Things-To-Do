package xyz.tcreopargh.amttd.api.json.response

import xyz.tcreopargh.amttd.api.data.CrudType

interface ICrudResponse<T> : IResponseBody {
    val operation: CrudType?
    val entity: T?
}