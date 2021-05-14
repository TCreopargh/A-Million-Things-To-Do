package xyz.tcreopargh.amttd.common.bean.request

import java.util.*

data class TodoEntryViewRequest(
    var groupId: UUID? = null
) : IRequestBody