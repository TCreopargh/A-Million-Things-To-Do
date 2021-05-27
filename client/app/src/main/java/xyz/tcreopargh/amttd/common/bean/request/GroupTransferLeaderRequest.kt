package xyz.tcreopargh.amttd.common.bean.request

import java.util.*

data class GroupTransferLeaderRequest(
    var groupId: UUID? = null,
    var actionPerformerId: UUID? = null,
    var targetUserId: UUID? = null
) : IRequestBody
