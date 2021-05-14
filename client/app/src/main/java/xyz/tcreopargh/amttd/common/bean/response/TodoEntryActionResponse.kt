package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.TodoEntryImpl

data class TodoEntryActionResponse(
    var operation: CrudType? = null,
    override var success: Boolean? = false,
    var entry: TodoEntryImpl? = null,
    override var error: Int? = null
) : IResponseBody
