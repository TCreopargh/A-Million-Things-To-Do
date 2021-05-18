package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.TodoEntryImpl

data class TodoEntryCrudResponse(
    override var operation: CrudType? = null,
    override var success: Boolean? = false,
    override var entity: TodoEntryImpl? = null,
    override var error: Int? = null
) : ICrudResponse<TodoEntryImpl>
