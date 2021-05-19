package xyz.tcreopargh.amttd_web.common.bean.request

import java.util.*

data class ChangeUsernameRequest(
    var newUsername: String? = null,
    var userId: UUID? = null
) : IRequestBody