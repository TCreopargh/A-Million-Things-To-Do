package xyz.tcreopargh.amttd_web.common.bean.request

import java.util.*

data class TodoEntryViewRequest(
    var groupId: UUID? = null
) : IRequestBody