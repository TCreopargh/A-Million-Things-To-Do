package xyz.tcreopargh.amttd.api.data

import java.io.Serializable
import java.util.*

/**
 * @author TCreopargh
 * The base class of all users
 */
interface IUser : Serializable {
    val username: String
    val email: String
    val uuid: UUID
}
