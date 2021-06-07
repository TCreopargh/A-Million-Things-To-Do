package xyz.tcreopargh.amttd_web.api.json.response

import xyz.tcreopargh.amttd_web.api.data.WorkGroupImpl

data class WorkGroupViewResponse(
    override var success: Boolean? = false,
    var workGroups: List<WorkGroupImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
