package xyz.tcreopargh.amttd.user

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

/**
 * @author TCreopargh
 * The base class of all users
 */
sealed class AbstractUser : Parcelable, Serializable {
    abstract var userName: String
    abstract val uuid: UUID
    abstract fun isLocalUser(): Boolean
}

/**
 * The user representing the current logged in user
 */
class LocalUser(
    override var userName: String,
    override val uuid: UUID,
    val authToken: String?
) : AbstractUser(), Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        UUID.fromString(parcel.readString()),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(uuid.toString())
        parcel.writeString(authToken)
    }

    override fun isLocalUser(): Boolean {
        return true
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalUser> {
        override fun createFromParcel(parcel: Parcel): LocalUser {
            return LocalUser(parcel)
        }

        override fun newArray(size: Int): Array<LocalUser?> {
            return arrayOfNulls(size)
        }
    }
}

/**
 * Any user that is not the currently logged in user
 */
class RemoteUser(
    override var userName: String,
    override val uuid: UUID
) : AbstractUser(), Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        UUID.fromString(parcel.readString())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(uuid.toString())
    }

    override fun isLocalUser(): Boolean {
        return false
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RemoteUser> {
        override fun createFromParcel(parcel: Parcel): RemoteUser {
            return RemoteUser(parcel)
        }

        override fun newArray(size: Int): Array<RemoteUser?> {
            return arrayOfNulls(size)
        }
    }
}
