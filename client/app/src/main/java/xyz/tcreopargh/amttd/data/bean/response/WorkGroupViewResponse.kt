package xyz.tcreopargh.amttd.data.bean.response

import xyz.tcreopargh.amttd.data.interactive.WorkGroupImpl

data class WorkGroupViewResponse(
    override var success: Boolean? = false,
    var workGroups: List<WorkGroupImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody