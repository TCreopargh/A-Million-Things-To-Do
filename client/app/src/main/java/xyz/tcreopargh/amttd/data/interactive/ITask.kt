package xyz.tcreopargh.amttd.data.interactive

import java.util.*

/**
 * @author TCreopargh
 */
interface ITask {
    val taskId: UUID
    val name: String
    val completed: Boolean
}