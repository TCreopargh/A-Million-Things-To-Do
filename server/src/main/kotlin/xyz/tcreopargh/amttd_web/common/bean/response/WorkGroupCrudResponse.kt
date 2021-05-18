package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.WorkGroupImpl

data class WorkGroupCrudResponse(
    override var operation: CrudType? = null,
    override var success: Boolean? = false,
    override var entity: WorkGroupImpl? = null,
    override var error: Int? = null
) : ICrudResponse<WorkGroupImpl>