package xyz.tcreopargh.amttd_web.api.data.action

import xyz.tcreopargh.amttd_web.api.data.UserImpl
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

    override val stringExtra: String
        get() = comment

    override val actionType: ActionType = ActionType.COMMENT
}