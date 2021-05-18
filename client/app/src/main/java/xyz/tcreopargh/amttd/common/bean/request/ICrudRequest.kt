package xyz.tcreopargh.amttd.common.bean.request

import xyz.tcreopargh.amttd.common.data.CrudType

/**
 * @author TCreopargh
 */
interface ICrudRequest<T> : IRequestBody {
    val operation: CrudType?
    val entity: T?
}