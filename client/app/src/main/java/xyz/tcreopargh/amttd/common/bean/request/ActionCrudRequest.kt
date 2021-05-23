package xyz.tcreopargh.amttd.common.bean.request

import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.action.ActionGeneric
import java.util.*

data class ActionCrudRequest(
    override var operation: CrudType? = null,
    override var entity: ActionGeneric? = null,
    var userId: UUID? = null,
    var entryId: UUID? = null
) : ICrudRequest<ActionGeneric>
