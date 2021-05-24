package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityTask
import xyz.tcreopargh.amttd_web.repository.TaskRepository
import java.util.*

@Service
class TaskService {
    @Autowired
    private lateinit var repository: TaskRepository
    fun getAll() = repository.findAll()
    fun getAllPresent() = repository.findAll().filter { it.isPresent }
    fun save(entry: EntityTask) = repository.save(entry)
    fun saveImmediately(entry: EntityTask) = repository.saveAndFlush(entry)
    fun findByIdOrNull(uuid: UUID) = repository.findByIdOrNull(uuid)?.takeIf { it.isPresent }

    @Deprecated("Use markAsRemoved", level = DeprecationLevel.WARNING)
    fun delete(uuid: UUID) = repository.deleteById(uuid)

    fun markAsRemoved(uuid: UUID) {
        val entity = repository.findByIdOrNull(uuid)
        if (entity != null) {
            entity.isPresent = false
            saveImmediately(entity)
        }
    }
}