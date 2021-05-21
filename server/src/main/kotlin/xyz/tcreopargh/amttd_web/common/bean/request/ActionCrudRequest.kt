package xyz.tcreopargh.amttd_web.common.bean.request

import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.action.ActionGeneric
import java.util.*

data class ActionCrudRequest(
    override var operation: CrudType? = null,
    override var entity: ActionGeneric? = null,
    var userId: UUID? = null
) : ICrudRequest<ActionGeneric>
