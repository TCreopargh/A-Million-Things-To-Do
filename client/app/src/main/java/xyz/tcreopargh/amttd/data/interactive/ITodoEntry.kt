package xyz.tcreopargh.amttd.data.interactive

import xyz.tcreopargh.amttd.data.interactive.action.IAction
import java.io.Serializable
import java.util.*

/**
 * @author TCreopargh
 */
interface ITodoEntry : Serializable {
    val entryId: UUID
    val creator: IUser?
    val title: String
    val description: String
    val status: TodoStatus
    val timeCreated: Calendar
    val deadline: Calendar
    val tasks: List<ITask>
    val actionHistory: List<IAction>
}