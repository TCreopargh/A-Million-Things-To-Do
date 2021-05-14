package xyz.tcreopargh.amttd_web.bean.response

import xyz.tcreopargh.amttd_web.data.TodoEntryImpl

data class TodoEntryViewResponse(
    override var success: Boolean? = false,
    var entries: List<TodoEntryImpl>? = listOf(),
    override var error: Exception? = null
) : IResponseBody
