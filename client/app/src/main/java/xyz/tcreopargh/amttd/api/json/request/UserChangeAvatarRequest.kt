package xyz.tcreopargh.amttd.api.json.request

import java.util.*

@Suppress("ArrayInDataClass")
data class UserChangeAvatarRequest(
    var userId: UUID? = null,
    var img: ByteArray? = null
)
