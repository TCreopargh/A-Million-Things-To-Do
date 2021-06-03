package xyz.tcreopargh.amttd.common.bean.request

import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.TodoEntryImpl
import java.util.*

data class TodoEntryCrudRequest(
    override var operation: CrudType? = null,

    /**
     * The to_do entry related to the action.
     *
     * If the action is *ADD*, the UUID will be ignored.
     *
     * If the action is *DELETE*, only the UUID will be used.
     */
    override var entity: TodoEntryImpl? = null,
    var userId: UUID? = null,
    var workGroupId: UUID? = null
) : ICrudRequest<TodoEntryImpl>