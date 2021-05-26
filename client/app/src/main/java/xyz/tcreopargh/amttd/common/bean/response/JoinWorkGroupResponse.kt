package xyz.tcreopargh.amttd.common.bean.response

data class JoinWorkGroupResponse(
    override var success: Boolean? = false,
    override var error: Int? = null
) : IResponseBody
