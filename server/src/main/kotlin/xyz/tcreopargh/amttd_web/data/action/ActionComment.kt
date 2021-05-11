package xyz.tcreopargh.amttd_web.data.action

import xyz.tcreopargh.amttd_web.data.UserImpl
import java.util.*

/**
 * @author TCreopargh
 */
class ActionComment(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    var comment: String
) : IAction {

    constructor(action: IAction) : this(
        actionId = action.actionId,
        user = action.user?.let { UserImpl(it) },
        timeCreated = action.timeCreated,
        comment = action.stringExtra ?: "",
    )

    override val stringExtra: String
        get() = comment

    override val actionType: ActionType = ActionType.COMMENT
}