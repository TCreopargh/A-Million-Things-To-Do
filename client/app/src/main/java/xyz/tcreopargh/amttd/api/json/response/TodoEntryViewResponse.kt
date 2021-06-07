package xyz.tcreopargh.amttd.api.json.response

import xyz.tcreopargh.amttd.api.data.TodoEntryImpl

data class TodoEntryViewResponse(
    override var success: Boolean? = false,
    var entries: List<TodoEntryImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
