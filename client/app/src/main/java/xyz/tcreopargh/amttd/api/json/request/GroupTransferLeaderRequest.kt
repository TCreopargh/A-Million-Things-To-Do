package xyz.tcreopargh.amttd.api.json.request

import java.util.*

data class GroupTransferLeaderRequest(
    var groupId: UUID? = null,
    var actionPerformerId: UUID? = null,
    var targetUserId: UUID? = null
) : IRequestBody
