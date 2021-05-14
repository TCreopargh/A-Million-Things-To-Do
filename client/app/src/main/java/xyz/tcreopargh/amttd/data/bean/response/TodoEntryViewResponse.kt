package xyz.tcreopargh.amttd.data.bean.response

import xyz.tcreopargh.amttd.data.interactive.TodoEntryImpl

data class TodoEntryViewResponse(
    override var success: Boolean? = false,
    var entries: List<TodoEntryImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
