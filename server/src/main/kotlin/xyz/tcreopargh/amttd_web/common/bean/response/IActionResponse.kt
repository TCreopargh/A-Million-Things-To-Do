package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.CrudType

interface IActionResponse: IResponseBody {
    val operation: CrudType?
}