package xyz.tcreopargh.amttd.data.interactive

import xyz.tcreopargh.amttd.data.interactive.ITask
import java.util.*

/**
 * @author TCreopargh
 */
data class TaskImpl(
    override val taskId: UUID = UUID.randomUUID(),
    override var name: String = "",
    override var completed: Boolean = false,
) : ITask {
    constructor(task: ITask) : this(
        taskId = task.taskId,
        name = task.name,
        completed = task.completed
    )
}