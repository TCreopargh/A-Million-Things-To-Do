package xyz.tcreopargh.amttd.common.bean.request

import java.util.*

data class AddCommentRequest(
    var userId: UUID? = null,
    var comment: String? = "",
    var entryId: UUID? = null
) : IRequestBody