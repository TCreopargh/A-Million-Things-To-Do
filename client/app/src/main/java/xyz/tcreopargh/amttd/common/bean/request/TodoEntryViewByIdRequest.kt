package xyz.tcreopargh.amttd.common.bean.request

import java.util.*

data class TodoEntryViewByIdRequest(
    var entryId: UUID? = null
) : IRequestBody
