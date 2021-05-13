package xyz.tcreopargh.amttd_web.bean

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TodoEntryViewBody(
    @JsonProperty(required = true)
    var groupId: UUID? = null
) : IRequestBody