package xyz.tcreopargh.amttd_web.common.data.action

import xyz.tcreopargh.amttd_web.common.data.UserImpl
import java.util.*

/**
 * @author TCreopargh
 */
class ActionTitleChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    // Changed title
    override val oldValue: String?,
    override val newValue: String?,
    override val timeCreated: Calendar
) : IAction {

    override val actionType: ActionType = ActionType.TITLE_CHANGED
}

class ActionDescriptionChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    // Changed description
    override val oldValue: String?,
    override val newValue: String?,
    override val timeCreated: Calendar
) : IAction {

    override val actionType: ActionType = ActionType.DESCRIPTION_CHANGED
}

class ActionDeadlineChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar
) : IAction {

    override val actionType: ActionType = ActionType.DEADLINE_CHANGED
}