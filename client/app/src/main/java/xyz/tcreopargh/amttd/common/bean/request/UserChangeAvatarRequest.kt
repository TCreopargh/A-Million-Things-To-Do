package xyz.tcreopargh.amttd.common.bean.request

import java.util.*

@Suppress("ArrayInDataClass")
data class UserChangeAvatarRequest(
    var userId: UUID? = null,
    var img: ByteArray? = null
)
