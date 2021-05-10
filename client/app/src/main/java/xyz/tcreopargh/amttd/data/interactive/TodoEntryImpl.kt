package xyz.tcreopargh.amttd.data.interactive

import xyz.tcreopargh.amttd.data.interactive.action.ActionGeneric
import xyz.tcreopargh.amttd.data.interactive.action.IAction
import java.util.*

/**
 * @author TCreopargh
 */
data class TodoEntryImpl(
    override val entryId: UUID = UUID.randomUUID(),
    override val creator: UserImpl? = null,
    override var title: String = "",
    override var description: String = "",
    override var status: TodoStatus = TodoStatus.NOT_STARTED,
    override val timeCreated: Calendar = Calendar.getInstance(),
    override var deadline: Calendar = Calendar.getInstance(),
    override var tasks: List<TaskImpl> = listOf(),
    override var actionHistory: List<ActionGeneric> = listOf(),
) : ITodoEntry {
    constructor(todoEntry: ITodoEntry) : this(
        todoEntry.entryId,
        todoEntry.creator?.run { UserImpl(this) },
        todoEntry.title,
        todoEntry.description,
        todoEntry.status,
        todoEntry.timeCreated,
        todoEntry.deadline,
        todoEntry.tasks.map { TaskImpl(it) },
        todoEntry.actionHistory.map { ActionGeneric(it) }
    )
}