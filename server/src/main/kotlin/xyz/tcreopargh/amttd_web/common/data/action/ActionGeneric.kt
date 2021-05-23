package xyz.tcreopargh.amttd_web.common.data.action

import xyz.tcreopargh.amttd_web.common.data.TaskImpl
import xyz.tcreopargh.amttd_web.common.data.TodoStatus
import xyz.tcreopargh.amttd_web.common.data.UserImpl
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
    override val task: TaskImpl? = null
) : IAction {

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
            ActionType.TASK_UNCOMPLETED -> ActionTaskCompleted(
                actionId = actionId,
                user = user,
                timeCreated = timeCreated,
                task = task ?: TaskImpl()
            )
        }
}