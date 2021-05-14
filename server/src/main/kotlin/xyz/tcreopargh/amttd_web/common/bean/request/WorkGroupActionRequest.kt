package xyz.tcreopargh.amttd_web.common.bean.request

import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.WorkGroupImpl

data class WorkGroupActionRequest(
    var operation: CrudType? = null,

    /**
     * The work group related to the action.
     * If the action is *ADD*, the UUID will be ignored.
     * If the action is *DELETE*, only the UUID will be used.
     */
    var workGroup: WorkGroupImpl? = null
) : IRequestBody