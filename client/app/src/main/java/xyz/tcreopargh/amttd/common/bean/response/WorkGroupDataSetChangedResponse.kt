package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.WorkGroupImpl

data class WorkGroupDataSetChangedResponse(
    override var success: Boolean? = false,
    var updatedWorkGroup: WorkGroupImpl? = null,
    override var error: Int? = null
) : IResponseBody