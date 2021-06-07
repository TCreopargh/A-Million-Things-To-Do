package xyz.tcreopargh.amttd.api.json.request

import java.util.*

data class ShareWorkGroupRequest(
    var userId: UUID? = null,
    var groupId: UUID? = null,
    var expirationTimeInDays: Int? = null
) : IRequestBody