package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.CrudType

interface IActionResponse<T> : IResponseBody {
    val operation: CrudType?
    val entity: T?
}