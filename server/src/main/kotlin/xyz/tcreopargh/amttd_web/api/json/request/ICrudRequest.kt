package xyz.tcreopargh.amttd_web.api.json.request

import xyz.tcreopargh.amttd_web.api.data.CrudType

/**
 * @author TCreopargh
 */
interface ICrudRequest<T> : IRequestBody {
    val operation: CrudType?
    val entity: T?
}