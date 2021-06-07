package xyz.tcreopargh.amttd_web.controller.crud

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.api.data.CrudType
import xyz.tcreopargh.amttd_web.api.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.api.exception.AmttdException
import xyz.tcreopargh.amttd_web.api.json.request.WorkGroupCrudRequest
import xyz.tcreopargh.amttd_web.api.json.response.WorkGroupCrudResponse
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.util.logger
import javax.servlet.http.HttpServletRequest

/**
 * @author TCreopargh
 *
 * Workgroup related stuff
 */
@RestController
@LoginRequired
class WorkGroupController : ControllerBase() {
    @PostMapping(
        "/workgroup",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun resolveAction(
        request: HttpServletRequest,
        @RequestBody
        body: WorkGroupCrudRequest
    ): WorkGroupCrudResponse {
        return try {
            val (workGroup: EntityWorkGroup?, user) = if (body.operation != CrudType.CREATE) {
                verifyWorkgroup(request, body.entity?.groupId, body.userId)
            } else {
                Pair(null, verifyUser(request, body.userId))
            }
            when (body.operation) {
                CrudType.READ -> {
                    workGroup ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                    WorkGroupCrudResponse(
                        operation = body.operation,
                        success = true,
                        entity = WorkGroupImpl(workGroup)
                    )
                }
                CrudType.UPDATE -> {
                    workGroup ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                    if (workGroup.leader?.uuid != user.uuid) {
                        throw AmttdException(AmttdException.ErrorCode.INSUFFICIENT_PERMISSION)
                    }
                    WorkGroupCrudResponse(
                        operation = body.operation,
                        success = true,
                        entity = WorkGroupImpl(workGroupService.saveImmediately(
                            workGroup.apply {
                                groupName = body.entity?.name?.replace("\n", " ")?.trim() ?: groupName
                            }
                        ))
                    )
                }
                CrudType.CREATE -> {

                    val entity = workGroupService.saveImmediately(EntityWorkGroup().apply {
                        groupName = body.entity?.name
                            ?: throw AmttdException(AmttdException.ErrorCode.ILLEGAL_ARGUMENTS)
                        leader = user
                    })
                    val obj = WorkGroupImpl(entity)
                    user.joinedWorkGroups = (user.joinedWorkGroups + entity).toMutableSet()
                    userService.saveImmediately(user)
                    WorkGroupCrudResponse(
                        operation = body.operation,
                        success = true,
                        entity = obj
                    )
                }
                CrudType.DELETE -> {
                    workGroup ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                    val userId = body.userId ?: throw AmttdException(AmttdException.ErrorCode.USER_NOT_FOUND)
                    if (workGroup.leader?.uuid != userId) {
                        user.joinedWorkGroups.removeIf { it.groupId == workGroup.groupId }
                        workGroup.users.removeIf { it.uuid == user.uuid }
                        workGroupService.saveImmediately(workGroup)
                        userService.saveImmediately(user)
                        WorkGroupCrudResponse(
                            operation = body.operation,
                            success = true
                        )
                    } else {
                        workGroupService.deleteById(workGroup.groupId)
                        WorkGroupCrudResponse(
                            operation = body.operation,
                            success = true
                        )
                    }
                }
                else -> throw AmttdException(AmttdException.ErrorCode.ACTION_NOT_SUPPORTED)
            }
        } catch (e: AmttdException) {
            logger.error("Error in work group crud: ", e)
            WorkGroupCrudResponse(
                operation = body.operation,
                success = false,
                error = e.errorCodeValue
            )
        }
    }
}