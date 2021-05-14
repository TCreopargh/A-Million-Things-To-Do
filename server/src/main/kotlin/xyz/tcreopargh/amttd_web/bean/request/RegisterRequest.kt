package xyz.tcreopargh.amttd_web.bean.request

data class RegisterRequest(
    var email: String? = null,
    var password: String? = null,
    var username: String? = null
) : IRequestBody
