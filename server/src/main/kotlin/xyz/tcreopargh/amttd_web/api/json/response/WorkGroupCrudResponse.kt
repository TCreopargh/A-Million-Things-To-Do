package xyz.tcreopargh.amttd_web.api.json.response

import xyz.tcreopargh.amttd_web.api.data.CrudType
import xyz.tcreopargh.amttd_web.api.data.WorkGroupImpl

data class WorkGroupCrudResponse(
    override var operation: CrudType? = null,
    override var success: Boolean? = false,
    override var entity: WorkGroupImpl? = null,
    override var error: Int? = null
) : ICrudResponse<WorkGroupImpl>