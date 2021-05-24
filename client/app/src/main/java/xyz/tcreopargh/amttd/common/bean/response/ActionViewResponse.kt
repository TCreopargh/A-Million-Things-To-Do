package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.action.ActionGeneric

data class ActionViewResponse(
    override var success: Boolean? = false,
    var actions: List<ActionGeneric>? = listOf(),
    override var error: Int? = null
) : IResponseBody

