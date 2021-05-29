package xyz.tcreopargh.amttd.data.user

import xyz.tcreopargh.amttd.common.data.IUser
import java.io.Serializable
import java.util.*

/**
 * The user representing the current logged in user
 */
data class LocalUser(
    override var username: String,
    override val uuid: UUID,
    override var email: String,
    val authToken: String?
) : IUser, Serializable
