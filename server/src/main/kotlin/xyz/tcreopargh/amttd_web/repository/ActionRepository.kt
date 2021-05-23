package xyz.tcreopargh.amttd_web.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.tcreopargh.amttd_web.entity.EntityAction
import java.util.*

@Repository
interface ActionRepository: JpaRepository<EntityAction, UUID> {
}