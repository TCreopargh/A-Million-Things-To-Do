package xyz.tcreopargh.amttd.common.bean.response

import xyz.tcreopargh.amttd.common.data.UserImpl

data class GroupUserViewResponse(
    override var success: Boolean? = false,
    var users: List<UserImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
