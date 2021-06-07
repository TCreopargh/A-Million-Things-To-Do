package xyz.tcreopargh.amttd.api.json.request

import java.util.*

data class TodoEntryViewRequest(
    var groupId: UUID? = null,
    var userId: UUID? = null
) : IRequestBody