package xyz.tcreopargh.amttd_web.common.bean.request

import java.util.*

data class UserProfileChangeRequest(
    var newUsername: String? = null,
    var newEmail: String? = null,
    var newPassword: String? = null,
    var userId: UUID? = null
) : IRequestBody