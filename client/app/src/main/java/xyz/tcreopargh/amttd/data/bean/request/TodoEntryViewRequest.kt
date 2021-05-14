package xyz.tcreopargh.amttd.data.bean.request

import xyz.tcreopargh.amttd.data.bean.request.IRequestBody
import java.util.*

data class TodoEntryViewRequest(
    var groupId: UUID? = null
) : IRequestBody