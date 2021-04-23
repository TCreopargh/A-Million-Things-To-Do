package xyz.tcreopargh.amttd.data.group

import xyz.tcreopargh.amttd.data.todo.TodoEntry
import xyz.tcreopargh.amttd.user.AbstractUser
import java.util.*

/**
 * @author TCreopargh
 */
data class WorkGroup(
    val uuid: UUID = UUID.randomUUID(),
    var name: String = "",
    val timeCreated: Calendar = Calendar.getInstance(),
    val entries: MutableList<TodoEntry> = mutableListOf(),
    val users: MutableList<AbstractUser> = mutableListOf()
)
