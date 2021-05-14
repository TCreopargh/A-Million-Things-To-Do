package xyz.tcreopargh.amttd_web.bean.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TodoEntryViewByIdRequest(
    @JsonProperty(required = true)
    var entryId: UUID? = null
) : IRequestBody
