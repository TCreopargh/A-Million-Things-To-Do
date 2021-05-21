package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.TodoEntryImpl
import xyz.tcreopargh.amttd_web.common.data.action.ActionGeneric

data class ActionCrudResponse(
    override var operation: CrudType? = null,
    override var success: Boolean? = false,
    override var entity: ActionGeneric? = null,
    override var error: Int? = null
) : ICrudResponse<ActionGeneric>