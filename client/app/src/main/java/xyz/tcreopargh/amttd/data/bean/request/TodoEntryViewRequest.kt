package xyz.tcreopargh.amttd.data.bean.request

import java.util.*

data class TodoEntryViewRequest(
    var groupId: UUID? = null
) : IRequestBody