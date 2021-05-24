package xyz.tcreopargh.amttd_web.common.data.action

import xyz.tcreopargh.amttd_web.common.data.ITask
import xyz.tcreopargh.amttd_web.common.data.TaskImpl
import xyz.tcreopargh.amttd_web.common.data.UserImpl
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

    override val actionType: ActionType = ActionType.TASK_COMPLETED
}

class ActionTaskUncompleted(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) :
    IActionTask {

    override val actionType: ActionType = ActionType.TASK_UNCOMPLETED
}

class ActionTaskAdded(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) : IActionTask {
    override val actionType: ActionType = ActionType.TASK_ADDED
}

class ActionTaskRemoved(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) : IActionTask {

    override val actionType: ActionType = ActionType.TASK_REMOVED
}

class ActionTaskEdited(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) : IActionTask {

    override val actionType: ActionType = ActionType.TASK_EDITED
}
