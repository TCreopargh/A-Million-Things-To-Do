package xyz.tcreopargh.amttd.data.bean.response

import xyz.tcreopargh.amttd.data.interactive.CrudType
import xyz.tcreopargh.amttd.data.interactive.TodoEntryImpl

data class TodoEntryActionResponse(
    var operation: CrudType? = null,
    override var success: Boolean? = false,
    var entry: TodoEntryImpl? = null,
    override var error: Int? = null
) : IResponseBody
