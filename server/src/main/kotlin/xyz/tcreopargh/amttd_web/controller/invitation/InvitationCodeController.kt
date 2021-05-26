package xyz.tcreopargh.amttd_web.controller.invitation

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.common.bean.request.ShareWorkGroupRequest
import xyz.tcreopargh.amttd_web.common.bean.response.ShareWorkGroupResponse
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityInvitationCode
import xyz.tcreopargh.amttd_web.util.generateInvitationCode
import javax.servlet.http.HttpServletRequest

@RestController
@LoginRequired
@RequestMapping("/workgroups")
class InvitationCodeController : ControllerBase() {
    // TODO: Implement response to invitation codes
    @PostMapping(
        "/share",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun shareWorkGroup(request: HttpServletRequest, @RequestBody body: ShareWorkGroupRequest): ShareWorkGroupResponse {
        return try {
            verifyWorkgroup(request, body.groupId)
            val invitationCode = generateInvitationCode()
            invitationCodeService.saveImmediately(
                EntityInvitationCode(
                    invitationCode = invitationCode,
                    user = userService.findByIdOrNull(
                        body.userId
                            ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                    ),
                    workGroup = workGroupService.findByIdOrNull(
                        body.groupId
                            ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                    ),
                    expirationTimeInDays = body.expirationTimeInDays ?: 7
                )
            )
            ShareWorkGroupResponse(
                success = true,
                invitationCode = invitationCode
            )
        } catch (e: Exception) {
            ShareWorkGroupResponse(success = false, error = AmttdException.getFromException(e).errorCodeValue)
        }
    }
}