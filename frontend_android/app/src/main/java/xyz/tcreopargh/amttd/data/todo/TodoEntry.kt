package xyz.tcreopargh.amttd.data.todo

import xyz.tcreopargh.amttd.data.todo.action.IAction
import xyz.tcreopargh.amttd.user.AbstractUser
import java.util.*
import java.util.stream.Collectors
import kotlin.math.absoluteValue

/**
 * @author TCreopargh
 */
data class TodoEntry(
    val creator: AbstractUser,
    var title: String,
    var description: String = "",
    var tasks: MutableList<Task> = mutableListOf(Task(title)),
    var status: Status = Status.NOT_STARTED,
    var actionHistory: MutableList<IAction> = mutableListOf(),
    val uuid: UUID = UUID.randomUUID(),
    val timeCreated: Calendar = Calendar.getInstance(),
    val deadline: Calendar? = null
) : Comparable<TodoEntry> {
    fun addAction(action: IAction) {
        actionHistory.add(action)
    }

    private fun completedTasks(): MutableList<Task> =
        tasks.stream().filter { it.isDone }.collect(Collectors.toList())

    fun getCompletionText() = "${completedTasks().size} / ${tasks.size}"

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

    override fun hashCode(): Int {
        var result = creator.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + tasks.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + actionHistory.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + timeCreated.hashCode()
        return result
    }
}