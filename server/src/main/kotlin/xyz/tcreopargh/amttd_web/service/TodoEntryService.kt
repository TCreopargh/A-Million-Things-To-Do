package xyz.tcreopargh.amttd_web.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.tcreopargh.amttd_web.entity.EntityTodoEntry
import xyz.tcreopargh.amttd_web.repository.TodoEntryRepository
import java.util.*

@Service
class TodoEntryService : RepositoryServiceBase<EntityTodoEntry, UUID>() {
    @Autowired
    override lateinit var repository: TodoEntryRepository
}