package xyz.tcreopargh.amttd_web.data.action

import xyz.tcreopargh.amttd_web.data.ITask
import xyz.tcreopargh.amttd_web.data.IUser
import xyz.tcreopargh.amttd_web.data.TaskImpl
import xyz.tcreopargh.amttd_web.data.UserImpl
import java.util.*

/**
 * @author TCreopargh
 */

interface IActionTask : IAction {
    override val task: ITask
}

class ActionTaskCompleted(
    override val user: UserImpl,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) :
    IActionTask {

    override val actionType: ActionType = ActionType.TASK_COMPLETED
}

class ActionTaskUncompleted(
    override val user: UserImpl,
    override val timeCreated: Calendar,
    override val task: TaskImpl
) :
    IActionTask {


    override val actionType: ActionType = ActionType.TASK_UNCOMPLETED
}