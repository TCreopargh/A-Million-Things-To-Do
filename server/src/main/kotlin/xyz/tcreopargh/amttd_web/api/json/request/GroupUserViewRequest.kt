package xyz.tcreopargh.amttd_web.api.json.request

import java.util.*

data class GroupUserViewRequest(
    var groupId: UUID? = null,
    var userId: UUID? = null
) : IRequestBody
