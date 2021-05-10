package xyz.tcreopargh.amttd_web.data

import java.io.Serializable
import java.util.*

/**
 * @author TCreopargh
 */
interface ITask : Serializable {
    val taskId: UUID
    val name: String
    val completed: Boolean
}