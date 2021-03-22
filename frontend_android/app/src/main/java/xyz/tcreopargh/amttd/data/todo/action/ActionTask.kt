package xyz.tcreopargh.amttd.data.todo.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.todo.Task
import xyz.tcreopargh.amttd.user.AbstractUser
import xyz.tcreopargh.amttd.util.i18n
import java.util.*

/**
 * @author TCreopargh
 */

interface IActionTask: IAction {
    val task: Task
}

class ActionTaskCompleted(override val user: AbstractUser, override val timeCreated: Calendar, override val task: Task) :
    IActionTask {
    override fun getActionText(): Spannable {
        return SpannableString(i18n(R.string.action_task_completed) + task.name)
    }
}

class ActionTaskUncompleted(override val user: AbstractUser, override val timeCreated: Calendar, override val task: Task) :
    IActionTask {
    override fun getActionText(): Spannable {
        return SpannableString(i18n(R.string.action_task_uncompleted) + task.name)
    }
}