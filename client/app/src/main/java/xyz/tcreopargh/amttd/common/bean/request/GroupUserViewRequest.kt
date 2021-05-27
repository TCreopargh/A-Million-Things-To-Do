package xyz.tcreopargh.amttd.common.bean.request

import java.util.*

data class GroupUserViewRequest(
    var groupId: UUID? = null,
    var userId: UUID? = null
) : IRequestBody
