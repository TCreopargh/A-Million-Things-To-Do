package xyz.tcreopargh.amttd.data.bean.request

import java.util.*

data class TodoEntryViewByIdRequest(
    var entryId: UUID? = null
) : IRequestBody
