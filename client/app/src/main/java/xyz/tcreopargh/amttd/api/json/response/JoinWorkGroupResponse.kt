package xyz.tcreopargh.amttd.api.json.response

data class JoinWorkGroupResponse(
    override var success: Boolean? = false,
    override var error: Int? = null
) : IResponseBody
