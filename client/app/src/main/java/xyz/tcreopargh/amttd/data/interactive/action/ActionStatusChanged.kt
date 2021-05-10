package xyz.tcreopargh.amttd.data.interactive.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.data.interactive.IUser
import xyz.tcreopargh.amttd.data.interactive.TodoStatus
import xyz.tcreopargh.amttd.data.interactive.UserImpl
import xyz.tcreopargh.amttd.util.plus
import java.util.*

/**
 * @author TCreopargh
 */
class ActionStatusChanged(
    override val user: UserImpl,
    override val timeCreated: Calendar,
    override val fromStatus: TodoStatus,
    override val toStatus: TodoStatus
) : IAction {
    override fun getActionText(): Spannable {
        return SpannableString("changed status from ") +
                fromStatus.getColoredString() +
                SpannableString(" to ") +
                toStatus.getColoredString()

    }
    override val actionType: ActionType = ActionType.STATUS_CHANGED
}