package xyz.tcreopargh.amttd.api.json.request

import java.util.*

data class WorkGroupViewRequest(
    var uuid: UUID? = null
) : IRequestBody