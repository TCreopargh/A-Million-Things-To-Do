package xyz.tcreopargh.amttd_web.data

import java.util.*

data class UserImpl(
    override var username: String = "",
    override var uuid: UUID = UUID.randomUUID()
) : IUser {
    constructor(user: IUser) : this(
        username = user.username,
        uuid = user.uuid
    )
}
