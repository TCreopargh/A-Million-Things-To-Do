package xyz.tcreopargh.amttd_web.controller.view

import com.google.gson.reflect.TypeToken
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.binding.TodoEntryViewBody
import xyz.tcreopargh.amttd_web.data.ITodoEntry
import xyz.tcreopargh.amttd_web.data.TodoEntryImpl
import xyz.tcreopargh.amttd_web.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.util.gson
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
class TodoEntryPresenter {
    @RequestMapping("/todo", method = [RequestMethod.POST])
    fun resolveWorkGroups(request: HttpServletRequest, @RequestBody body: TodoEntryViewBody): String {
        var entries: Set<ITodoEntry> = setOf()
        body.groupId?.let {
        }
        val list = entries.stream().map {
            TodoEntryImpl(it)
        }.collect(Collectors.toList())
        return gson.toJson(list, object : TypeToken<List<WorkGroupImpl>>() {}.type)
    }
}