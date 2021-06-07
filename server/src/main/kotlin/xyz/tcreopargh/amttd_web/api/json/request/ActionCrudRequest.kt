package xyz.tcreopargh.amttd_web.api.json.request

import xyz.tcreopargh.amttd_web.api.data.CrudType
import xyz.tcreopargh.amttd_web.api.data.action.ActionGeneric
import java.util.*

data class ActionCrudRequest(
    override var operation: CrudType? = null,
    override var entity: ActionGeneric? = null,
    var userId: UUID? = null,
    var entryId: UUID? = null
) : ICrudRequest<ActionGeneric>
