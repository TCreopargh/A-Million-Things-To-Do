package xyz.tcreopargh.amttd.api.json.request

data class RegisterRequest(
    var email: String? = null,
    var password: String? = null,
    var username: String? = null,
    var clientVersion: Long? = null
) : IRequestBody
