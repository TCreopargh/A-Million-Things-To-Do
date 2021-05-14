package xyz.tcreopargh.amttd_web.bean.request

import com.fasterxml.jackson.annotation.JsonProperty
import xyz.tcreopargh.amttd_web.data.CrudType
import xyz.tcreopargh.amttd_web.data.TodoEntryImpl

data class TodoEntryActionRequest(
    @JsonProperty(required = true)
    var operation: CrudType? = null,

    /**
     * The to_do entry related to the action.
     * If the action is *ADD*, the UUID will be ignored.
     * If the action is *DELETE*, only the UUID will be used.
     */
    var todoEntry: TodoEntryImpl? = null
) : IRequestBody