package xyz.tcreopargh.amttd_web.api.json.request

import java.util.*

data class JoinWorkGroupRequest(
    var userId: UUID? = null,
    var invitationCode: String? = null
) : IRequestBody