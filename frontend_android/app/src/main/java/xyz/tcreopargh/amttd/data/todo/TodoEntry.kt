package xyz.tcreopargh.amttd.data.todo

import xyz.tcreopargh.amttd.data.todo.action.IAction
import xyz.tcreopargh.amttd.user.AbstractUser
import java.util.*
import kotlin.math.absoluteValue

/**
 * @author TCreopargh
 */
data class TodoEntry(
    val creator: AbstractUser,
    var title: String,
    var description: String = "",
    var tasks: MutableList<Task> = mutableListOf(),
    var status: Status = Status.NOT_STARTED,
    var actionHistory: MutableList<IAction> = mutableListOf(),
    val uuid: UUID = UUID.randomUUID(),
    val timeCreated: Calendar = Calendar.getInstance()
) : Comparable<TodoEntry> {
    fun addAction(action: IAction) {
        actionHistory.add(action)
    }

    override fun compareTo(other: TodoEntry): Int {
        if (this.status.isActive() && !other.status.isActive()) {
            return 1
        }
        if (!this.status.isActive() && other.status.isActive()) {
            return -1
        }
        if (this.status.sortOrder.absoluteValue != other.status.sortOrder.absoluteValue) {
            return -1 * this.status.sortOrder.absoluteValue.compareTo(other.status.sortOrder.absoluteValue)
        }
        return this.timeCreated.compareTo(other.timeCreated)
    }

    override fun equals(other: Any?): Boolean {
        return this.uuid == (other as? TodoEntry)?.uuid
    }
}