package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.WorkGroupImpl

data class WorkGroupActionResponse(
    override var operation: CrudType? = null,
    override var success: Boolean? = false,
    var workGroup: WorkGroupImpl? = null,
    override var error: Int? = null
) : IActionResponse