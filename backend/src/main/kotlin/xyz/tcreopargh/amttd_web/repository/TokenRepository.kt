package xyz.tcreopargh.amttd_web.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.entity.AuthToken
import java.util.*

@Repository
interface TokenRepository : JpaRepository<AuthToken, String> {
    fun findByUser(user: EntityUser): List<AuthToken>
}