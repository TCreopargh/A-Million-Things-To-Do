package xyz.tcreopargh.amttd_web.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import java.util.*

@Repository
interface WorkGroupRepository : JpaRepository<EntityWorkGroup, UUID> {
    fun findAllByUsers(user: EntityUser): List<EntityWorkGroup>
}