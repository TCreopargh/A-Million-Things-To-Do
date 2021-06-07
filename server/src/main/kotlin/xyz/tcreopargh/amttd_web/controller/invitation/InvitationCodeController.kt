package xyz.tcreopargh.amttd_web.controller.invitation

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.api.exception.AmttdException
import xyz.tcreopargh.amttd_web.api.json.request.JoinWorkGroupRequest
import xyz.tcreopargh.amttd_web.api.json.request.ShareWorkGroupRequest
import xyz.tcreopargh.amttd_web.api.json.response.JoinWorkGroupResponse
import xyz.tcreopargh.amttd_web.api.json.response.ShareWorkGroupResponse
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityInvitationCode
import xyz.tcreopargh.amttd_web.util.generateInvitationCode
import javax.servlet.http.HttpServletRequest

/**
 * @author TCreopargh
 *
 * handle invitation codes.
 */
@RestController
@LoginRequired
@RequestMapping("/workgroups")
class InvitationCodeController : ControllerBase() {
    @PostMapping(
        "/share",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun shareWorkGroup(request: HttpServletRequest, @RequestBody body: ShareWorkGroupRequest): ShareWorkGroupResponse {
        return try {
            val (workGroup, user) = verifyWorkgroup(request, body.groupId, body.userId)
            val invitationCode = generateInvitationCode()
            if (invitationCodeService.findByCode(invitationCode) != null) {
                throw AmttdException(AmttdException.ErrorCode.UNIQUE_ID_CONFLICT)
            }
            invitationCodeService.saveImmediately(
                EntityInvitationCode(
                    invitationCode = invitationCode,
                    user = user,
                    workGroup = workGroup,
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

    @PostMapping(
        "/join",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun joinWorkGroup(request: HttpServletRequest, @RequestBody body: JoinWorkGroupRequest): JoinWorkGroupResponse {
        return try {
            val user = verifyUser(request, body.userId)
            val invitationCode = invitationCodeService.findByCode(
                body.invitationCode ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
            ) ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
            if (invitationCode.isExpired()) {
                invitationCodeService.remove(invitationCode)
                throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_INVALID)
            }
            val workGroup =
                invitationCode.workGroup ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
            if (workGroup.users.contains(user)) {
                throw AmttdException(AmttdException.ErrorCode.ALREADY_IN_WORKGROUP)
            }
            workGroup.users.add(user)
            user.joinedWorkGroups.add(workGroup)
            userService.saveImmediately(user)
            workGroupService.saveImmediately(workGroup)
            JoinWorkGroupResponse(success = true)
        } catch (e: Exception) {
            JoinWorkGroupResponse(success = false, error = AmttdException.getFromException(e).errorCodeValue)
        }
    }
}