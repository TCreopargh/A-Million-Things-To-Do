package xyz.tcreopargh.amttd_web.controller.invitation

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.common.bean.request.ActionCrudRequest
import xyz.tcreopargh.amttd_web.common.bean.request.ShareWorkGroupRequest
import xyz.tcreopargh.amttd_web.common.bean.response.ShareWorkGroupResponse
import javax.servlet.http.HttpServletRequest

@RestController
@LoginRequired
class InvitationCodeController {
    // TODO: Implement generate and respond to invitation codes
    @PostMapping(
        "/workgroups/share",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun shareWorkGroup(request: HttpServletRequest, @RequestBody body: ShareWorkGroupRequest) : ShareWorkGroupResponse {
        TODO("Not Implemented")
    }
}