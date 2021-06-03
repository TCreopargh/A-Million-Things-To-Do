package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityTask
import xyz.tcreopargh.amttd_web.repository.TaskRepository
import java.util.*

@Service
class TaskService : RepositoryServiceBase<EntityTask, UUID>() {
    @Autowired
    override lateinit var repository: TaskRepository
    fun getAllPresent() = repository.findAll().filter { it.isPresent }

    @Deprecated(
        "Use markAsRemoved",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("markAsRemoved")
    )
    fun delete(uuid: UUID) = repository.deleteById(uuid)

    fun markAsRemoved(uuid: UUID) {
        val entity = repository.findByIdOrNull(uuid)
        if (entity != null) {
            entity.isPresent = false
            saveImmediately(entity)
        }
    }
}