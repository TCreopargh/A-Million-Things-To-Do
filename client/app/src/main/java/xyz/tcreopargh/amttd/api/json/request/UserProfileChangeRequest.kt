package xyz.tcreopargh.amttd.api.json.request

import java.util.*

data class UserProfileChangeRequest(
    var newUsername: String? = null,
    var newEmail: String? = null,
    var newPassword: String? = null,
    var userId: UUID? = null
) : IRequestBody