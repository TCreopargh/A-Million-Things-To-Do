package xyz.tcreopargh.amttd_web.controller.view

import com.google.gson.reflect.TypeToken
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import xyz.tcreopargh.amttd_web.binding.WorkGroupViewBody
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.util.gson
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@Controller
class WorkGroupPresenter : ControllerBase() {

    @RequestMapping("/workgroups", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resolveWorkGroups(request: HttpServletRequest, @RequestBody body: WorkGroupViewBody): String {
        var workGroups: Set<EntityWorkGroup> = setOf()
        body.uuid?.let { it ->
            val user = userService.findById(it)
            user?.joinedWorkGroups?.run Groups@{
                workGroups = this@Groups
            }
        }
        return gson.toJson(workGroups.stream().map {
            WorkGroupImpl(it)
        }.collect(Collectors.toList()), object : TypeToken<List<WorkGroupImpl>>() {}.type)
    }
}