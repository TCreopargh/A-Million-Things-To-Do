package xyz.tcreopargh.amttd.common.bean.request

import java.util.*

data class JoinWorkGroupRequest (
    var userId: UUID? = null,
    var invitationCode: String? = null
): IRequestBody