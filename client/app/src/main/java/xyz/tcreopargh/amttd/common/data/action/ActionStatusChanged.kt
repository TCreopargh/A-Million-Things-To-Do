package xyz.tcreopargh.amttd.common.data.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.data.TodoStatus
import xyz.tcreopargh.amttd.common.data.UserImpl
import xyz.tcreopargh.amttd.util.getColor
import xyz.tcreopargh.amttd.util.i18n
import xyz.tcreopargh.amttd.util.plus
import xyz.tcreopargh.amttd.util.setColor
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
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.design_default_color_primary)) +
                SpannableString(i18n(R.string.change_status)) +
                fromStatus.getColoredString() +
                SpannableString(i18n(R.string.change_status_to)) +
                toStatus.getColoredString()
    }

    override val actionType: ActionType = ActionType.STATUS_CHANGED
}