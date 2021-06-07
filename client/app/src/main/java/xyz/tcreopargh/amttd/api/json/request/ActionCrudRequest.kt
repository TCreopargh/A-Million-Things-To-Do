package xyz.tcreopargh.amttd.api.json.request

import xyz.tcreopargh.amttd.api.data.CrudType
import xyz.tcreopargh.amttd.api.data.action.ActionGeneric
import java.util.*

data class ActionCrudRequest(
    override var operation: CrudType? = null,
    override var entity: ActionGeneric? = null,
    var userId: UUID? = null,
    var entryId: UUID? = null
) : ICrudRequest<ActionGeneric>
