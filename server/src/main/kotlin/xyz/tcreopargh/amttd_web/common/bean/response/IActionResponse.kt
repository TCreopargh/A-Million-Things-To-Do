package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.CrudType

interface IActionResponse<T> : IResponseBody {
    val operation: CrudType?
    val entity: T?
}