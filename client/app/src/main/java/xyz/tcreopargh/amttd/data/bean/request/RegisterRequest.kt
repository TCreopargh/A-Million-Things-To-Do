package xyz.tcreopargh.amttd.data.bean.request

data class RegisterRequest(
    var email: String? = null,
    var password: String? = null,
    var username: String? = null
) : IRequestBody
