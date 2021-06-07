package xyz.tcreopargh.amttd_web.api.json.response

import xyz.tcreopargh.amttd_web.api.data.TodoEntryImpl

data class TodoEntryViewResponse(
    override var success: Boolean? = false,
    var entries: List<TodoEntryImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
