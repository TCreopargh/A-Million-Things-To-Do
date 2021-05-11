package xyz.tcreopargh.amttd.data.interactive.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.interactive.ITask
import xyz.tcreopargh.amttd.data.interactive.TaskImpl
import xyz.tcreopargh.amttd.data.interactive.UserImpl
import xyz.tcreopargh.amttd.util.i18n
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
        return SpannableString(i18n(R.string.action_task_completed) + task.name)
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
        return SpannableString(i18n(R.string.action_task_uncompleted) + task.name)
    }

    override val actionType: ActionType = ActionType.TASK_UNCOMPLETED
}