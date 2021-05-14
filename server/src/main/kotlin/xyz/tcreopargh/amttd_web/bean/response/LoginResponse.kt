package xyz.tcreopargh.amttd_web.bean.response

import java.util.*

data class LoginResponse(
    override var success: Boolean? = false,
    var email: String? = null,
    var username: String? = null,
    var uuid: UUID? = null,
    var token: String? = null,
    override var error: Int? = null
) : IResponseBody
