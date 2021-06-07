package xyz.tcreopargh.amttd_web.controller.workgroup

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.api.data.UserImpl
import xyz.tcreopargh.amttd_web.api.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.api.exception.AmttdException
import xyz.tcreopargh.amttd_web.api.json.request.GroupRemoveUserRequest
import xyz.tcreopargh.amttd_web.api.json.request.GroupTransferLeaderRequest
import xyz.tcreopargh.amttd_web.api.json.request.GroupUserViewRequest
import xyz.tcreopargh.amttd_web.api.json.response.GroupUserViewResponse
import xyz.tcreopargh.amttd_web.api.json.response.WorkGroupDataSetChangedResponse
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.util.logger
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

/**
 * @author TCreopargh
 * Manage users in a workgroup.
 */
@RestController
@LoginRequired
@RequestMapping("/workgroup/users")
class WorkGroupUserController : ControllerBase() {

    @PostMapping(
        "",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getUserList(request: HttpServletRequest, @RequestBody body: GroupUserViewRequest): GroupUserViewResponse {
        return try {
            val (workGroup, _) = verifyWorkgroup(request, body.groupId, body.userId)
            var users: Set<EntityUser> = setOf()

            body.groupId?.let {
                users = workGroup.users
            }
            val list = users.stream().map {
                UserImpl(it)
            }.collect(Collectors.toList())
            list.sortByDescending { it.username }
            GroupUserViewResponse(success = true, users = list)
        } catch (e: Exception) {
            logger.error("Exception in WorkGroupUserController: ", e)
            GroupUserViewResponse(success = false, error = AmttdException.ErrorCode.getFromException(e).value)
        }
    }

    @Suppress("DuplicatedCode")
    @PostMapping(
        "/kick",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun kickUser(
        request: HttpServletRequest,
        @RequestBody body: GroupRemoveUserRequest
    ): WorkGroupDataSetChangedResponse {
        return try {
            val (workGroup, _) = verifyWorkgroup(request, body.groupId, body.actionPerformerId, true)
            val targetUser = userService.findByIdOrNull(
                body.targetUserId ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
            ) ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
            if (!workGroup.users.contains(targetUser)) {
                throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
            }
            targetUser.joinedWorkGroups.removeIf { it.groupId == workGroup.groupId }
            workGroup.users.removeIf { it.uuid == targetUser.uuid }
            userService.saveImmediately(targetUser)
            workGroupService.saveImmediately(workGroup)
            WorkGroupDataSetChangedResponse(success = true, updatedWorkGroup = WorkGroupImpl(workGroup))
        } catch (e: Exception) {
            logger.error("Exception in WorkGroupUserController: ", e)
            WorkGroupDataSetChangedResponse(success = false, error = AmttdException.ErrorCode.getFromException(e).value)
        }
    }

    @Suppress("DuplicatedCode")
    @PostMapping(
        "/transfer",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun transferLeadership(
        request: HttpServletRequest,
        @RequestBody body: GroupTransferLeaderRequest
    ): WorkGroupDataSetChangedResponse {
        return try {
            val (workGroup, _) = verifyWorkgroup(request, body.groupId, body.actionPerformerId, true)
            val targetUser = userService.findByIdOrNull(
                body.targetUserId ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
            ) ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
            if (!workGroup.users.contains(targetUser)) {
                throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
            }
            workGroup.leader = targetUser
            workGroupService.saveImmediately(workGroup)
            WorkGroupDataSetChangedResponse(success = true, updatedWorkGroup = WorkGroupImpl(workGroup))
        } catch (e: Exception) {
            logger.error("Exception in WorkGroupUserController: ", e)
            WorkGroupDataSetChangedResponse(success = false, error = AmttdException.ErrorCode.getFromException(e).value)
        }
    }
}