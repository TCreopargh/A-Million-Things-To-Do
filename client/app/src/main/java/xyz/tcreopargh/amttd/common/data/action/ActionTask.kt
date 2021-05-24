package xyz.tcreopargh.amttd.common.data.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.data.ITask
import xyz.tcreopargh.amttd.common.data.TaskImpl
import xyz.tcreopargh.amttd.common.data.UserImpl
import xyz.tcreopargh.amttd.util.getColor
import xyz.tcreopargh.amttd.util.i18n
import xyz.tcreopargh.amttd.util.plus
import xyz.tcreopargh.amttd.util.setColor
import java.util.*

/**
 * @author TCreopargh
 */

interface IActionTask : IAction {
    override val task: ITask
}

class ActionTaskCompleted(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) :
    IActionTask {
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.action_task_completed) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_COMPLETED
    override fun getImageRes(): Int = R.drawable.ic_baseline_check_circle_outline_24
}

class ActionTaskUncompleted(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) :
    IActionTask {
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.action_task_uncompleted) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_UNCOMPLETED
    override fun getImageRes(): Int = R.drawable.ic_baseline_remove_circle_outline_24
}

class ActionTaskAdded(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) :
    IActionTask {
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.action_task_added) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_ADDED

    override fun getImageRes(): Int = R.drawable.ic_baseline_add_task_24
}

class ActionTaskRemoved(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) :
    IActionTask {
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.action_task_removed) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_REMOVED
    override fun getImageRes(): Int = R.drawable.ic_baseline_delete_outline_24
}

class ActionTaskEdited(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) : IActionTask {
    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(i18n(R.string.action_task_edited) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_EDITED
    override fun getImageRes(): Int = R.drawable.ic_baseline_edit_24
}
