package xyz.tcreopargh.amttd.api.json.response

import xyz.tcreopargh.amttd.api.data.CrudType
import xyz.tcreopargh.amttd.api.data.TodoEntryImpl

data class TodoEntryCrudResponse(
    override var operation: CrudType? = null,
    override var success: Boolean? = false,
    override var entity: TodoEntryImpl? = null,
    override var error: Int? = null
) : ICrudResponse<TodoEntryImpl>
