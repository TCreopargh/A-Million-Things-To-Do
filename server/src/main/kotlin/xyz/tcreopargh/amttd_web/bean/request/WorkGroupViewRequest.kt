package xyz.tcreopargh.amttd_web.bean.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class WorkGroupViewRequest(
    @JsonProperty(required = true)
    var uuid: UUID? = null
) : IRequestBody