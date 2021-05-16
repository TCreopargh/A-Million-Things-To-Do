package xyz.tcreopargh.amttd_web.controller.crud

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.common.bean.request.WorkGroupActionRequest
import xyz.tcreopargh.amttd_web.common.bean.response.WorkGroupActionResponse
import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import javax.servlet.http.HttpServletRequest

@RestController
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
        body: WorkGroupActionRequest
    ): WorkGroupActionResponse {
        return try {
            val id =
                body.workGroup?.groupId ?: throw AmttdException(AmttdException.ErrorCode.JSON_MISSING_FIELD)
            val workGroup = workGroupService.findByIdOrNull(id)
                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
            when (body.operation) {
                CrudType.READ -> {
                    WorkGroupActionResponse(
                        operation = body.operation,
                        success = true,
                        workGroup = WorkGroupImpl(workGroup)
                    )
                }
                CrudType.UPDATE -> {

                    WorkGroupActionResponse(
                        operation = body.operation,
                        success = true,
                        workGroup = WorkGroupImpl(workGroupService.saveImmediately(
                            workGroup.apply {
                                groupName = body.workGroup?.name ?: groupName
                            }
                        ))
                    )
                }
                else -> throw AmttdException(AmttdException.ErrorCode.ACTION_NOT_SUPPORTED)
            }
        } catch (e: AmttdException) {
            WorkGroupActionResponse(
                operation = body.operation,
                success = false,
                error = e.errorCodeValue
            )
        }
    }
}