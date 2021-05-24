package xyz.tcreopargh.amttd.common.data.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.data.UserImpl
import xyz.tcreopargh.amttd.util.getColor
import xyz.tcreopargh.amttd.util.i18n
import xyz.tcreopargh.amttd.util.plus
import xyz.tcreopargh.amttd.util.setColor
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
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.change_title)) +
                SpannableString(" $oldValue ") +
                SpannableString(i18n(R.string.change_status_to)) +
                SpannableString(" $newValue")
    }

    override val actionType: ActionType = ActionType.TITLE_CHANGED

    override fun getImageRes(): Int = R.drawable.ic_baseline_access_alarm_24
}

class ActionDescriptionChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    // Changed description
    override val oldValue: String?,
    override val newValue: String?,
    override val timeCreated: Calendar
) : IAction {
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.change_description)) +
                SpannableString(" $oldValue ") +
                SpannableString(i18n(R.string.change_status_to)) +
                SpannableString(" $newValue")
    }

    override val actionType: ActionType = ActionType.DESCRIPTION_CHANGED

    override fun getImageRes(): Int = R.drawable.ic_baseline_access_alarm_24
}

class ActionDeadlineChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar
) : IAction {
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.change_deadline))
    }

    override val actionType: ActionType = ActionType.DEADLINE_CHANGED

    override fun getImageRes(): Int = R.drawable.ic_baseline_access_alarm_24
}