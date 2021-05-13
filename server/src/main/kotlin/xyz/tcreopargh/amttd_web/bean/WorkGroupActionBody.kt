package xyz.tcreopargh.amttd_web.bean

import com.fasterxml.jackson.annotation.JsonProperty
import xyz.tcreopargh.amttd_web.data.CrudType
import xyz.tcreopargh.amttd_web.data.WorkGroupImpl

data class WorkGroupActionBody(
    @JsonProperty(required = true)
    var operation: CrudType? = null,

    /**
     * The work group related to the action.
     * If the action is *ADD*, the UUID will be ignored.
     * If the action is *DELETE*, only the UUID will be used.
     */
    var workGroup: WorkGroupImpl? = null
) : IRequestBody