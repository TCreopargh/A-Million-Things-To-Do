package xyz.tcreopargh.amttd_web.controller.view

import com.google.gson.reflect.TypeToken
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.binding.TodoEntryByIdViewBody
import xyz.tcreopargh.amttd_web.binding.TodoEntryViewBody
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.data.TodoEntryImpl
import xyz.tcreopargh.amttd_web.entity.EntityTodoEntry
import xyz.tcreopargh.amttd_web.util.gson
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
class TodoEntryPresenter : ControllerBase() {
    @RequestMapping("/todo", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resolveWorkGroups(request: HttpServletRequest, @RequestBody body: TodoEntryViewBody): String {
        var entries: List<EntityTodoEntry> = listOf()
        body.groupId?.let {
            entries = workGroupService.findByIdOrNull(it)?.entries ?: listOf()
        }
        val list = entries.stream().filter { it != null }.map {
            TodoEntryImpl(it)
        }.collect(Collectors.toList())
        return gson.toJson(list, object : TypeToken<List<TodoEntryImpl>>() {}.type)
    }

    @RequestMapping("/todo-entry", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resolveTodoEntry(request: HttpServletRequest, @RequestBody body: TodoEntryByIdViewBody): String {
        var entry: EntityTodoEntry? = null
        body.entryId?.let {
            entry = todoEntryService.findByIdOrNull(it)
        }
        val ret = entry?.let { TodoEntryImpl(it) }

        return if (ret != null) gson.toJson(ret, object : TypeToken<TodoEntryImpl>() {}.type) else "{}"
    }
}