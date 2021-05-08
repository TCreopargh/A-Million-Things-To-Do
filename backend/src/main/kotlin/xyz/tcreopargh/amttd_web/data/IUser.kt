package xyz.tcreopargh.amttd_web.data
import java.io.Serializable
import java.util.*

/**
 * @author TCreopargh
 * The base class of all users
 */
interface IUser : Serializable {
    var username: String
    val uuid: UUID
}