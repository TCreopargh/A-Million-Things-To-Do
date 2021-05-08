package xyz.tcreopargh.amttd.data.interactive

import java.util.*

data class UserImpl(
    override var username: String = "",
    override var uuid: UUID = UUID.randomUUID()
) : IUser {
    constructor(user: IUser) : this(user.username, user.uuid)
}
