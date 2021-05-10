package xyz.tcreopargh.amttd.data.interactive.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.interactive.*
import xyz.tcreopargh.amttd.util.i18n
import java.util.*

/**
 * @author TCreopargh
 * action type for serialization.
 */
data class ActionGeneric(
    override val user: UserImpl,
    override val timeCreated: Calendar,
    override val actionType: ActionType,
    override var stringExtra: String?,
    override val fromStatus: TodoStatus?,
    override val toStatus: TodoStatus?,
    override val task: TaskImpl?
) : IAction {

    override fun getActionText(): Spannable = SpannableString(i18n(R.string.unknown_action))

    constructor(action: IAction) : this(
        user = UserImpl(action.user),
        timeCreated = action.timeCreated,
        actionType = action.actionType,
        stringExtra = action.stringExtra,
        fromStatus = action.fromStatus,
        toStatus = action.toStatus,
        task = action.task?.run {TaskImpl(this)}
    )

    val action: IAction
        get() = when (actionType) {
            ActionType.COMMENT          -> ActionComment(
                user = user,
                timeCreated = timeCreated,
                comment = stringExtra ?: ""
            )
            ActionType.STATUS_CHANGED   -> ActionStatusChanged(
                user = user,
                timeCreated = timeCreated,
                fromStatus = fromStatus ?: TodoStatus.NOT_STARTED,
                toStatus = toStatus ?: TodoStatus.NOT_STARTED
            )
            ActionType.TASK_COMPLETED   -> ActionTaskCompleted(
                user = user,
                timeCreated = timeCreated,
                task = task ?: TaskImpl()
            )
            ActionType.TASK_UNCOMPLETED -> ActionTaskCompleted(
                user = user,
                timeCreated = timeCreated,
                task = task ?: TaskImpl()
            )
        }
}