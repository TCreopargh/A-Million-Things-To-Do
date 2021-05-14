package xyz.tcreopargh.amttd.common.bean.request

import java.util.*

data class WorkGroupViewRequest(
    var uuid: UUID? = null
) : IRequestBody