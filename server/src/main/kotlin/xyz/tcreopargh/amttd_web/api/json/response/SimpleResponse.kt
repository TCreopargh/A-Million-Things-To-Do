package xyz.tcreopargh.amttd_web.api.json.response

data class SimpleResponse(
    override var success: Boolean? = false,
    override var error: Int? = null
) : IResponseBody