package xyz.tcreopargh.amttd.data.todo

import java.util.*

/**
 * @author TCreopargh
 */
data class Task(
    var name: String,
    var isDone: Boolean = false,
    val uuid: UUID = UUID.randomUUID()
)
