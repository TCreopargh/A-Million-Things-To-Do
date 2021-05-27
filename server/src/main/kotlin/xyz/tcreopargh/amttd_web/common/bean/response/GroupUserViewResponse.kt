package xyz.tcreopargh.amttd_web.common.bean.response

import xyz.tcreopargh.amttd_web.common.data.UserImpl

data class GroupUserViewResponse(
    override var success: Boolean? = false,
    var users: List<UserImpl>? = listOf(),
    override var error: Int? = null
) : IResponseBody
