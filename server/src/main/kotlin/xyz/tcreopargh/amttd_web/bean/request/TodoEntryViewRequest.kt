package xyz.tcreopargh.amttd_web.bean.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TodoEntryViewRequest(
    @JsonProperty(required = true)
    var groupId: UUID? = null
) : IRequestBody