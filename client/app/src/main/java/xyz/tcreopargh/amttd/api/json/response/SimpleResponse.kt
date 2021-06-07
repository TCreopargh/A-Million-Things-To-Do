package xyz.tcreopargh.amttd.api.json.response

data class SimpleResponse(
    override var success: Boolean? = false,
    override var error: Int? = null
) : IResponseBody