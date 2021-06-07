package xyz.tcreopargh.amttd_web.api.json.response

import xyz.tcreopargh.amttd_web.api.data.CrudType
import xyz.tcreopargh.amttd_web.api.data.TodoEntryImpl

data class TodoEntryCrudResponse(
    override var operation: CrudType? = null,
    override var success: Boolean? = false,
    override var entity: TodoEntryImpl? = null,
    override var error: Int? = null
) : ICrudResponse<TodoEntryImpl>
