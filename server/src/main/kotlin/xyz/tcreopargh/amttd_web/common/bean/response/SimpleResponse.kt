package xyz.tcreopargh.amttd_web.common.bean.response

data class SimpleResponse(
    override val success: Boolean?,
    override val error: Int?
) : IResponseBody