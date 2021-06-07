package xyz.tcreopargh.amttd.api.json.response

data class ShareWorkGroupResponse(
    override var success: Boolean? = false,
    var invitationCode: String? = null,
    override var error: Int? = null
) : IResponseBody
