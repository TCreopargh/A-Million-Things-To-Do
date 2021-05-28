package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.WorkGroupImpl

data class WorkGroupDataSetChangedResponse(
    override var success: Boolean? = false,
    var updatedWorkGroup: WorkGroupImpl? = null,
    override var error: Int? = null
) : IResponseBody