package xyz.tcreopargh.amttd_web.common.bean.response

data class SimpleResponse(
    override val success: Boolean? = false,
    override val error: Int? = null
) : IResponseBody