package xyz.tcreopargh.amttd_web.controller.view

import com.google.gson.reflect.TypeToken
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import xyz.tcreopargh.amttd_web.bean.request.WorkGroupViewRequest
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.util.gson
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
class WorkGroupPresenter : ControllerBase() {

    @PostMapping("/workgroups", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resolveWorkGroups(request: HttpServletRequest, @RequestBody body: WorkGroupViewRequest): String {
        var workGroups: Set<EntityWorkGroup> = setOf()
        body.uuid?.let {
            val user = userService.findById(it)
            user?.joinedWorkGroups?.run Groups@{
                workGroups = this@Groups
            }
        }
        val list = workGroups.stream().map {
            WorkGroupImpl(it)
        }.collect(Collectors.toList())
        return gson.toJson(list, object : TypeToken<List<WorkGroupImpl>>() {}.type)
    }
}