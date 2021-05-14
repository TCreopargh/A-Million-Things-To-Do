package xyz.tcreopargh.amttd.data.bean.response

import java.util.*

data class LoginResponse(
    override var success: Boolean? = false,
    var email: String? = null,
    var username: String? = null,
    var uuid: UUID? = null,
    var token: String? = null,
    var reason: String? = null,
    override var error: Exception? = null
) : IResponseBody
