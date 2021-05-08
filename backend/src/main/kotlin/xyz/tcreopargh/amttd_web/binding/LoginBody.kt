package xyz.tcreopargh.amttd_web.binding

data class LoginBody(
    var username: String? = null,
    var password: String? = null,
    var token: String? = null,
    var uuid: String? = null
)
