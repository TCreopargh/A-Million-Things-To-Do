package xyz.tcreopargh.amttd_web.api.json.request

import java.util.*

data class LoginRequest(
    var email: String? = null,
    var password: String? = null,
    var token: String? = null,
    var uuid: UUID? = null,
    var clientVersion: Long? = null
) : IRequestBody
