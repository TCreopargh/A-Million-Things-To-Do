package xyz.tcreopargh.amttd_web.api.json.response

import xyz.tcreopargh.amttd_web.api.data.UserImpl

data class GroupUserViewResponse(
    override var success: Boolean? = false,
    var users: List<UserImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
