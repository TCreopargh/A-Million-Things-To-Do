package xyz.tcreopargh.amttd_web.data.action

import xyz.tcreopargh.amttd_web.data.ITask
import xyz.tcreopargh.amttd_web.data.IUser
import xyz.tcreopargh.amttd_web.data.TodoStatus
import java.io.Serializable
import java.util.*

/**
 * @author TCreopargh
 * Actions are operations that are done to a TodoEntry.
 */
interface IAction : Serializable {
    /**
     * The user who initiated the action
     */
    val user: IUser
    val timeCreated: Calendar
    val actionType: ActionType

    /**
     * These properties might not exist
     */
    val stringExtra: String?
        get() = null
    val fromStatus: TodoStatus?
        get() = null
    val toStatus: TodoStatus?
        get() = null
    val task: ITask?
        get() = null
}
