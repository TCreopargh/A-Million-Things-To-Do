package xyz.tcreopargh.amttd_web.bean.request

import com.fasterxml.jackson.annotation.JsonProperty

data class RegisterRequest(
    var email: String? = null,
    var password: String? = null,
    var username: String? = null
) : IRequestBody
