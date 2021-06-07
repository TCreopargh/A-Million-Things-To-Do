package xyz.tcreopargh.amttd.api.json.request

import xyz.tcreopargh.amttd.api.data.CrudType

/**
 * @author TCreopargh
 */
interface ICrudRequest<T> : IRequestBody {
    val operation: CrudType?
    val entity: T?
}