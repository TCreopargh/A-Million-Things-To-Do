package xyz.tcreopargh.amttd_web.api.json.request

import java.util.*

data class WorkGroupViewRequest(
    var uuid: UUID? = null
) : IRequestBody