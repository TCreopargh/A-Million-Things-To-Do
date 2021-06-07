package xyz.tcreopargh.amttd.api.json.request

import xyz.tcreopargh.amttd.api.data.CrudType
import xyz.tcreopargh.amttd.api.data.TodoEntryImpl
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