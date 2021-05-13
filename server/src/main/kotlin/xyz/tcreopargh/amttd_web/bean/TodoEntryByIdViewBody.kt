package xyz.tcreopargh.amttd_web.bean

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TodoEntryByIdViewBody(
    @JsonProperty(required = true)
    var entryId: UUID? = null
) : IRequestBody
