package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.action.ActionGeneric

data class ActionViewResponse(
    override var success: Boolean? = false,
    var actions: List<ActionGeneric>? = listOf(),
    override var error: Int? = null
) : IResponseBody

