package xyz.tcreopargh.amttd_web.api.data.action

import xyz.tcreopargh.amttd_web.api.data.TodoStatus
import xyz.tcreopargh.amttd_web.api.data.UserImpl
import java.util.*

/**
 * @author TCreopargh
 */
class ActionStatusChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val fromStatus: TodoStatus,
    override val toStatus: TodoStatus
) : IAction {
    override val actionType: ActionType = ActionType.STATUS_CHANGED
}