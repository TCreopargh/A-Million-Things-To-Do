package xyz.tcreopargh.amttd_web.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import xyz.tcreopargh.amttd_web.entity.EntityAuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import javax.transaction.Transactional

@Repository
interface TokenRepository : JpaRepository<EntityAuthToken, String> {
    fun findByUser(user: EntityUser): List<EntityAuthToken>
    @Transactional
    @Modifying
    fun deleteByUser(user: EntityUser)
}