package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityTodoEntry
import xyz.tcreopargh.amttd_web.repository.TodoEntryRepository
import java.util.*

@Service
class TodoEntryService {
    @Autowired
    private lateinit var repository: TodoEntryRepository

    fun getAll() = repository.findAll()
    fun save(entry: EntityTodoEntry) = repository.save(entry)
    fun saveImmediately(entry: EntityTodoEntry) = repository.saveAndFlush(entry)
    fun findById(uuid: UUID) = repository.findById(uuid)
    fun findByIdOrNull(uuid: UUID) = repository.findByIdOrNull(uuid)
}