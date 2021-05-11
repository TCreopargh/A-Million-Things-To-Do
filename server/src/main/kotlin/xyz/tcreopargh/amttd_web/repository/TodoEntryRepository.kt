package xyz.tcreopargh.amttd_web.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.tcreopargh.amttd_web.entity.EntityTodoEntry
import xyz.tcreopargh.amttd_web.entity.EntityUser
import java.util.*

@Repository
interface TodoEntryRepository : JpaRepository<EntityTodoEntry, UUID> {
    fun findByCreator(user: EntityUser)
}