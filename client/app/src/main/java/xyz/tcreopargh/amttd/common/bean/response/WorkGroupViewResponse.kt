package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.WorkGroupImpl

data class WorkGroupViewResponse(
    override var success: Boolean? = false,
    var workGroups: List<WorkGroupImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody