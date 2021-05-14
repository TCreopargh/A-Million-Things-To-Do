package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.TodoEntryImpl

data class TodoEntryViewResponse(
    override var success: Boolean? = false,
    var entries: List<TodoEntryImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
