package xyz.tcreopargh.amttd_web.bean

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class WorkGroupViewBody(
    @JsonProperty(required = true)
    var uuid: UUID? = null
) : IRequestBody