package xyz.tcreopargh.amttd.common.data.action

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.data.UserImpl
import xyz.tcreopargh.amttd.util.*
import java.text.SimpleDateFormat
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
    override val timeCreated: Calendar,
    override var oldValue: String?,
    override var newValue: String?
) : IAction {

    companion object {
        @SuppressLint("SimpleDateFormat")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.change_deadline,
                    oldDeadline?.run { format() + " " + format(true) }
                        ?: i18n(R.string.deadline_not_set),
                    newDeadline?.run { format() + " " + format(true) }
                        ?: i18n(R.string.deadline_not_set)
                ))
    }

    var newDeadline: Calendar?
        get() = try {
            newValue?.let { str ->
                dateFormat.parse(str)?.let {
                    Calendar.getInstance().apply { time = it }
                }
            }
        } catch (_: Exception) {
            null
        }
        set(value) = try {
            value?.time?.let {
                newValue = dateFormat.format(it)
            } ?: Unit
        } catch (e: Exception) {
        }

    var oldDeadline: Calendar?
        get() = try {
            oldValue?.let { str ->
                dateFormat.parse(str)?.let {
                    Calendar.getInstance().apply { time = it }
                }
            }
        } catch (_: Exception) {
            null
        }
        set(value) = try {
            value?.time?.let {
                oldValue = dateFormat.format(it)
            } ?: Unit
        } catch (e: Exception) {
        }

    override val actionType: ActionType = ActionType.DEADLINE_CHANGED

    override fun getImageRes(): Int = R.drawable.ic_baseline_access_alarm_24
}