package xyz.tcreopargh.amttd_web.common.bean.request

import xyz.tcreopargh.amttd_web.common.data.CrudType

/**
 * @author TCreopargh
 */
interface ICrudRequest<T> : IRequestBody {
    val operation: CrudType?
    val entity: T?
}