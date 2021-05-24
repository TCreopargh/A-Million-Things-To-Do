package xyz.tcreopargh.amttd.common.data.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.data.TaskImpl
import xyz.tcreopargh.amttd.common.data.TodoStatus
import xyz.tcreopargh.amttd.common.data.UserImpl
import xyz.tcreopargh.amttd.util.i18n
import java.util.*

/**
 * @author TCreopargh
 * action type for serialization.
 */
data class ActionGeneric(
    override val actionId: UUID = UUID.randomUUID(),
    override val user: UserImpl? = null,
    override val timeCreated: Calendar = Calendar.getInstance(),
    override val actionType: ActionType = ActionType.COMMENT,
    override var stringExtra: String? = null,
    override val fromStatus: TodoStatus? = null,
    override val toStatus: TodoStatus? = null,
    override val task: TaskImpl? = null,
    override var oldValue: String? = null,
    override var newValue: String? = null
) : IAction {

    override fun getActionText(): Spannable = SpannableString(i18n(R.string.unknown_action))

    constructor(action: IAction) : this(
        actionId = action.actionId,
        user = action.user?.let { UserImpl(it) },
        timeCreated = action.timeCreated,
        actionType = action.actionType,
        stringExtra = action.stringExtra,
        fromStatus = action.fromStatus,
        toStatus = action.toStatus,
        task = action.task?.run { TaskImpl(this) }
    )

    val action: IAction
        get() = when (actionType) {
            ActionType.COMMENT          -> ActionComment(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated,
                comment = stringExtra ?: ""
            )
            ActionType.STATUS_CHANGED   -> ActionStatusChanged(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated,
                fromStatus = fromStatus ?: TodoStatus.NOT_STARTED,
                toStatus = toStatus ?: TodoStatus.NOT_STARTED
            )
            ActionType.TASK_COMPLETED   -> ActionTaskCompleted(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated,
                task = task ?: TaskImpl()
            )
            ActionType.TASK_UNCOMPLETED -> ActionTaskUncompleted(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated,
                task = task ?: TaskImpl()
            )
            ActionType.TASK_ADDED       -> ActionTaskAdded(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated,
                task = task ?: TaskImpl()
            )
            ActionType.TASK_REMOVED     -> ActionTaskRemoved(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated,
                task = task ?: TaskImpl()
            )
            ActionType.TASK_EDITED      -> ActionTaskEdited(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated,
                task = task ?: TaskImpl()
            )
            ActionType.DEADLINE_CHANGED -> ActionDeadlineChanged(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated
            )
            ActionType.TITLE_CHANGED -> ActionTitleChanged(
                actionId = actionId,
                user = user,
                oldValue = oldValue,
                newValue = newValue,
                timeCreated = timeCreated
            )
            ActionType.DESCRIPTION_CHANGED -> ActionDescriptionChanged(
                actionId = actionId,
                user = user,
                oldValue = oldValue,
                newValue = newValue,
                timeCreated = timeCreated
            )
        }
}