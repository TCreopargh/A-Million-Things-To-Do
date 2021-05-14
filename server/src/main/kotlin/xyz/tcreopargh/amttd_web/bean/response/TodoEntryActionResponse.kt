package xyz.tcreopargh.amttd_web.bean.response

import xyz.tcreopargh.amttd_web.data.CrudType
import xyz.tcreopargh.amttd_web.data.TodoEntryImpl

data class TodoEntryActionResponse(
    var operation: CrudType? = null,
    override var success: Boolean? = false,
    var entry: TodoEntryImpl? = null,
    override var error: Exception? = null
) : IResponseBody
