package xyz.tcreopargh.amttd_web.data

import java.io.Serializable
import java.util.*

/**
 * @author TCreopargh
 */
interface IWorkGroup: Serializable {
    val groupId: UUID
    var name: String
    var timeCreated: Calendar

    val usersInGroup: List<IUser> get() = listOf()
}
