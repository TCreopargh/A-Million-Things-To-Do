package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.CrudType

interface ICrudResponse<T> : IResponseBody {
    val operation: CrudType?
    val entity: T?
}