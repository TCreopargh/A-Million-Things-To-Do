package xyz.tcreopargh.amttd.api.json.response

import xyz.tcreopargh.amttd.api.data.UserImpl

data class GroupUserViewResponse(
    override var success: Boolean? = false,
    var users: List<UserImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
