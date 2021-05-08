package xyz.tcreopargh.amttd.data.interactive

import java.io.Serializable
import java.util.*

/**
 * @author TCreopargh
 * The base class of all users
 */
interface IUser : Serializable {
    val username: String
    val uuid: UUID
}