package xyz.tcreopargh.amttd_web.bean.response

import xyz.tcreopargh.amttd_web.data.WorkGroupImpl

data class WorkGroupViewResponse(
    override var success: Boolean? = false,
    var workGroups: List<WorkGroupImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
