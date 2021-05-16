package xyz.tcreopargh.amttd_web.common.bean.request

import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.TodoEntryImpl

data class TodoEntryActionRequest(
    var operation: CrudType? = null,

    /**
     * The to_do entry related to the action.
     * If the action is *ADD*, the UUID will be ignored.
     * If the action is *DELETE*, only the UUID will be used.
     */
    var todoEntry: TodoEntryImpl? = null
) : IRequestBody