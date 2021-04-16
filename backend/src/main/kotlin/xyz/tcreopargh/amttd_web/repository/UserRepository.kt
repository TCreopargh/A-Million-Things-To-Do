package xyz.tcreopargh.amttd_web.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import xyz.tcreopargh.amttd_web.entity.EntityUser
import java.util.*

@Repository
interface UserRepository : CrudRepository<EntityUser, UUID> {
    fun findByUsername(name: String): List<EntityUser>
}