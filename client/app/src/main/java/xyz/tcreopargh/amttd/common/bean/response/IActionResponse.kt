package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.CrudType

interface IActionResponse: IResponseBody {
    val operation: CrudType?
}