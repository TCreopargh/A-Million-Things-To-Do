package xyz.tcreopargh.amttd_web.bean

import java.util.*

data class LoginBody(
    var email: String? = null,
    var password: String? = null,
    var token: String? = null,
    var uuid: UUID? = null
) : IRequestBody
