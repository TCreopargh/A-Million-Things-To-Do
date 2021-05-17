package xyz.tcreopargh.amttd.common.bean.request

import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.WorkGroupImpl
import java.util.*

data class WorkGroupActionRequest(
    override var operation: CrudType? = null,

    /**
     * The work group related to the action.
     * If the action is *ADD*, the UUID will be ignored.
     * If the action is *DELETE*, only the UUID will be used.
     */
    override var entity: WorkGroupImpl? = null,
    var userId: UUID? = null
) : IActionRequest<WorkGroupImpl>