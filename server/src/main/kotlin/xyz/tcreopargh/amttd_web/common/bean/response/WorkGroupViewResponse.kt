package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.WorkGroupImpl

data class WorkGroupViewResponse(
    override var success: Boolean? = false,
    var workGroups: List<WorkGroupImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
