package xyz.tcreopargh.amttd.data.user

import android.os.Parcel
import android.os.Parcelable
import xyz.tcreopargh.amttd.common.data.IUser
import java.io.Serializable
import java.util.*

/**
 * The user representing the current logged in user
 */
class LocalUser(
    override var username: String,
    override val uuid: UUID,
    override var email: String,
    val authToken: String?
) : IUser, Serializable {

    override fun toString(): String {
        return "{username=$username, uuid=$uuid, authToken=$authToken}"
    }
}

/**
 * Any user that is not the currently logged in user
 */
