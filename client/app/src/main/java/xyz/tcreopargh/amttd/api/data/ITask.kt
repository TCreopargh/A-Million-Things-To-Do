package xyz.tcreopargh.amttd.api.data

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