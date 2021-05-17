package xyz.tcreopargh.amttd.common.bean.request

import xyz.tcreopargh.amttd.common.data.CrudType

/**
 * @author TCreopargh
 */
interface IActionRequest <T> : IRequestBody {
    val operation: CrudType?
    val entity: T?
}