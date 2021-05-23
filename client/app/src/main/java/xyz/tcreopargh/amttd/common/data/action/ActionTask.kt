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
            .setColor(getColor(R.color.design_default_color_primary)) +
                SpannableString(i18n(R.string.action_task_completed) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_COMPLETED
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
            .setColor(getColor(R.color.design_default_color_primary)) +
                SpannableString(i18n(R.string.action_task_uncompleted) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_UNCOMPLETED
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
            .setColor(getColor(R.color.design_default_color_primary)) +
                SpannableString(i18n(R.string.action_task_added) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_ADDED
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
            .setColor(getColor(R.color.design_default_color_primary)) +
                SpannableString(i18n(R.string.action_task_removed) + " " + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_REMOVED
}
