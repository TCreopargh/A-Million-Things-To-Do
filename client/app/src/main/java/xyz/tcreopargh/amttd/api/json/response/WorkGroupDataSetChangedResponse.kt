package xyz.tcreopargh.amttd.api.json.response

import xyz.tcreopargh.amttd.api.data.WorkGroupImpl

data class WorkGroupDataSetChangedResponse(
    override var success: Boolean? = false,
    var updatedWorkGroup: WorkGroupImpl? = null,
    override var error: Int? = null
) : IResponseBody