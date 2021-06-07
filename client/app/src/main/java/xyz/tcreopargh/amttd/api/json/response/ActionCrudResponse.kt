package xyz.tcreopargh.amttd.api.json.response

import xyz.tcreopargh.amttd.api.data.CrudType
import xyz.tcreopargh.amttd.api.data.action.ActionGeneric

data class ActionCrudResponse(
    override var operation: CrudType? = null,
    override var success: Boolean? = false,
    override var entity: ActionGeneric? = null,
    override var error: Int? = null
) : ICrudResponse<ActionGeneric>