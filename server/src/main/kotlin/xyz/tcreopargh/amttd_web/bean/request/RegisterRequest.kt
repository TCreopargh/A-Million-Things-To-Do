package xyz.tcreopargh.amttd_web.bean.request

import com.fasterxml.jackson.annotation.JsonProperty

data class RegisterRequest(
    @JsonProperty(required = true)
    var email: String? = null,
    @JsonProperty(required = true)
    var password: String? = null,
    var username: String? = null
) : IRequestBody
