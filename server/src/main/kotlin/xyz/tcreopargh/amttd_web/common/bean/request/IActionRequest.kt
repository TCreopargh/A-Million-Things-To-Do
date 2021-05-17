package xyz.tcreopargh.amttd_web.common.bean.request

import xyz.tcreopargh.amttd_web.common.data.CrudType

/**
 * @author TCreopargh
 */
interface IActionRequest<T> : IRequestBody {
    val operation: CrudType?
    val entity: T?
}