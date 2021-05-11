package xyz.tcreopargh.amttd_web.controller.view

import com.google.gson.reflect.TypeToken
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.binding.WorkGroupViewBody
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.util.gson
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
class WorkGroupPresenter : ControllerBase() {

    @RequestMapping("/workgroups", method = [RequestMethod.POST])
    fun resolveWorkGroups(request: HttpServletRequest, @RequestBody body: WorkGroupViewBody): String {
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