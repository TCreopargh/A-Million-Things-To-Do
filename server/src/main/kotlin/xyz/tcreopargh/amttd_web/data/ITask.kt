package xyz.tcreopargh.amttd_web.data

import java.util.*

/**
 * @author TCreopargh
 */
interface ITask {
    val taskId: UUID
    val name: String
    val completed: Boolean
}