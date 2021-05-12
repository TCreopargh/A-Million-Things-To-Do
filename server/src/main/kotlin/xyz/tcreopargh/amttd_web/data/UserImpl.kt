package xyz.tcreopargh.amttd_web.data

import java.util.*

data class UserImpl(
    override var username: String = "",
    override var email: String = "",
    override var uuid: UUID = UUID.randomUUID()
) : IUser {
    constructor(user: IUser) : this(
        username = user.username,
        email = user.email,
        uuid = user.uuid
    )
}
